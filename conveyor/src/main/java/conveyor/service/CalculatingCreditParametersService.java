package conveyor.service;

import conveyor.dto.CreditDTO;
import conveyor.dto.PaymentScheduleElement;
import conveyor.dto.ScoringDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CalculatingCreditParametersService {

    public void validateScoringDataDTO(ScoringDataDTO scoringDataDTO) {
        if (scoringDataDTO == null) {
            throw new RuntimeException("scoringDataDTO is null!");
        }
        if (scoringDataDTO.getIsInsuranceEnabled() == null) {
            throw new RuntimeException("isInsuranceEnabled in scoringDataDTO is null!");
        }
        if (scoringDataDTO.getIsSalaryClient() == null) {
            throw new RuntimeException("isSalaryClient in scoringDataDTO is null!");
        }
        if (scoringDataDTO.getAmount() == null) {
            throw new RuntimeException("amount in scoringDataDTO is null!");
        }
        if (scoringDataDTO.getTerm() == null) {
            throw new RuntimeException("term in scoringDataDTO is null!");
        }
    }

    public CreditDTO createCreditDTO(ScoringDataDTO scoringDataDTO, BigDecimal rate) {
        log.info("start of creating ScoringDataDTO");

        validateScoringDataDTO(scoringDataDTO);
        if (rate == null) {
            throw new RuntimeException("rate is null!");
        }

        BigDecimal creditAmount = scoringDataDTO.getAmount();
        log.info("creditAmount calculated as {}", creditAmount);

        BigDecimal monthlyPayment = CreationLoanOffersService.calcMonthlyPayment(rate, scoringDataDTO.getTerm(), creditAmount);
        log.info("monthlyPayment calculated as {}", monthlyPayment);

        List<PaymentScheduleElement> paymentScheduleElementList = createPaymentSchedule(creditAmount, scoringDataDTO.getTerm(), rate, monthlyPayment);

        if (scoringDataDTO.getIsInsuranceEnabled()) {
            creditAmount = creditAmount.subtract(new BigDecimal(CreationLoanOffersService.INSURANCE_PRICE));
        }

        BigDecimal psk = calcPsk(paymentScheduleElementList, creditAmount);

        CreditDTO creditDTO = CreditDTO.builder()
                .amount(scoringDataDTO.getAmount())
                .term(scoringDataDTO.getTerm())
                .rate(rate)
                .isInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled())
                .isSalaryClient(scoringDataDTO.getIsSalaryClient())
                .monthlyPayment(monthlyPayment)
                .paymentSchedule(paymentScheduleElementList)
                .psk(psk)
                .build();

        log.info("ScoringDataDTO created");
        return creditDTO;
    }

    public List<PaymentScheduleElement> createPaymentSchedule(BigDecimal creditAmount, int term, BigDecimal rate, BigDecimal monthlyPayment) {
        log.info("start of creating the payment schedule");
        if (creditAmount == null) {
            throw new RuntimeException("creditAmount is null!");
        }
        if (rate == null) {
            throw new RuntimeException("rate is null!");
        }
        if (monthlyPayment == null) {
            throw new RuntimeException("monthlyPayment is null!");
        }

        List<PaymentScheduleElement> paymentScheduleElementList = new ArrayList<>();

        BigDecimal remainingDebt = creditAmount;

        for (int i = 1; i <= term; i++) {
            LocalDate paymentDate = LocalDate.now().plusMonths(i);

            BigDecimal interestPayment = remainingDebt.multiply(rate).multiply(new BigDecimal(paymentDate.lengthOfMonth()))
                    .divide(new BigDecimal(paymentDate.lengthOfYear()), 3, RoundingMode.HALF_UP)
                    .divide(new BigDecimal(100), 3, RoundingMode.HALF_UP);

            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
            remainingDebt = remainingDebt.subtract(debtPayment);

            PaymentScheduleElement paymentScheduleElement = PaymentScheduleElement.builder()
                    .number(i)
                    .date(paymentDate)
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment)
                    .totalPayment(monthlyPayment)
                    .remainingDebt(remainingDebt)
                    .build();

            paymentScheduleElementList.add(paymentScheduleElement);
            log.info("one payment added to payment schedule. options: number: {}, paymentDate: {}, interestPayment: {},debtPayment: {}," +
                    " remainingDebt: {} ", i, paymentDate, interestPayment, debtPayment, remainingDebt);
        }

        remainingDebt = paymentScheduleElementList.get(term - 2).getRemainingDebt();

        PaymentScheduleElement paymentScheduleElement = paymentScheduleElementList.get(term - 1);
        paymentScheduleElement.setTotalPayment(remainingDebt);
        paymentScheduleElement.setDebtPayment(remainingDebt.subtract(paymentScheduleElement.getInterestPayment()));
        paymentScheduleElement.setRemainingDebt(new BigDecimal(0));
        log.info("last payment corrected");

        log.info("payment schedule created");
        return paymentScheduleElementList;
    }

    public BigDecimal calcPsk(List<PaymentScheduleElement> paymentScheduleElementList, BigDecimal creditAmount) {
        log.info("start of calculating psk");
        if (paymentScheduleElementList == null) {
            throw new RuntimeException("paymentScheduleElementList is null!");
        }
        if (creditAmount == null) {
            throw new RuntimeException("creditAmount is null!");
        }

        List<LocalDate> dates = new ArrayList<>();
        List<BigDecimal> payments = new ArrayList<>();

        for (PaymentScheduleElement paymentScheduleElement : paymentScheduleElementList) {
            dates.add(paymentScheduleElement.getDate());
            payments.add(paymentScheduleElement.getTotalPayment());

        }
        dates.add(0, LocalDate.now());
        payments.add(0, creditAmount.negate());
        log.info("dates of payments added to separate list. dates: {} ", dates);
        log.info("all payments added to separate list. payments: {} ", payments);

        BigDecimal basePeriod = new BigDecimal(30);
        BigDecimal countBasePeriods = new BigDecimal(365).divide(basePeriod, 3, RoundingMode.HALF_UP);

        List<BigDecimal> daysSinceDeliveryToEachPayment = new ArrayList<>();
        for (int k = 0; k <= paymentScheduleElementList.size(); k++) {
            int daysToPayment = (int) Duration.between(dates.get(0).atStartOfDay(), dates.get(k).atStartOfDay()).toDays();
            daysSinceDeliveryToEachPayment.add(new BigDecimal(daysToPayment));
        }
        log.info("the number of days from the moment the loan is issued to each payment added to separate list. " +
                "daysSinceDeliveryToEachPayment: {} ", daysSinceDeliveryToEachPayment);

        List<BigDecimal> eList = new ArrayList<>();
        List<BigDecimal> qList = new ArrayList<>();
        for (int k = 0; k <= paymentScheduleElementList.size(); k++) {

            eList.add(daysSinceDeliveryToEachPayment.get(k).remainder(basePeriod).divide(basePeriod, 3, RoundingMode.HALF_UP));
            qList.add(daysSinceDeliveryToEachPayment.get(k).divide(basePeriod, 3, RoundingMode.FLOOR));
        }
        log.info("eList created in accordance with the law \"353-ФЗ\". eList: {}  ", eList);
        log.info("qList created in accordance with the law \"353-ФЗ\". qList: {}  ", qList);

        BigDecimal accuracy = new BigDecimal("0.00001");

        BigDecimal sum = new BigDecimal(1);

        BigDecimal i = new BigDecimal(0);

        while (sum.doubleValue() > 0) {
            sum = new BigDecimal(0);
            for (int k = 0; k <= paymentScheduleElementList.size(); k++) {
                BigDecimal firstPartDivider = eList.get(k).multiply(i).add(new BigDecimal(1));
                BigDecimal secondPartDivider = (new BigDecimal(1).add(i)).pow(qList.get(k).intValue());
                sum = payments.get(k).divide((firstPartDivider.multiply(secondPartDivider, MathContext.DECIMAL64)), 7, RoundingMode.HALF_EVEN).add(sum);
            }
            i = i.add(accuracy);
        }
        log.info("i calculated. i is: {}", i);

        BigDecimal psk = i.multiply(countBasePeriods).multiply(new BigDecimal(100));
        log.info("psk calculated. psk is: {}", psk);
        return psk;
    }
}
