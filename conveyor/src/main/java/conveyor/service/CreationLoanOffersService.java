package conveyor.service;

import conveyor.dto.LoanApplicationRequestDTO;
import conveyor.dto.LoanOfferDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class CreationLoanOffersService {
    public static final int INSURANCE_PRICE = 15000;
    public static Long OFFERS_COUNT = 0L;

    @Autowired
    private Environment env;

    public List<LoanOfferDTO> createLoanOffersList(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("start of creating the list of loan offers");
        if (loanApplicationRequestDTO == null) {
            throw new RuntimeException("loanApplicationRequestDTO is null!");
        }
        List<LoanOfferDTO> loanOfferDTOS = new ArrayList<>();

        loanOfferDTOS.add(createLoanOffer(loanApplicationRequestDTO, true, true));
        log.info("created first loan offer (with insurance, as a salary client)");
        loanOfferDTOS.add(createLoanOffer(loanApplicationRequestDTO, false, false));
        log.info("created second loan offer (without insurance, not as a salary client)");
        loanOfferDTOS.add(createLoanOffer(loanApplicationRequestDTO, true, false));
        log.info("created third loan offer (with insurance, not as a salary client)");
        loanOfferDTOS.add(createLoanOffer(loanApplicationRequestDTO, false, true));
        log.info("created fourth loan offer (without insurance, as a salary client)");

        RateComparator rateComparator = new RateComparator();
        loanOfferDTOS.sort(rateComparator);
        log.info("the list of loan offers created");
        return loanOfferDTOS;
    }

    static class RateComparator implements Comparator<LoanOfferDTO> {
        @Override
        public int compare(LoanOfferDTO loanOfferDTO, LoanOfferDTO t1) {
            return t1.getRate().subtract(loanOfferDTO.getRate()).intValue();
        }
    }

    public LoanOfferDTO createLoanOffer(LoanApplicationRequestDTO loanApplicationRequestDTO, boolean isInsuranceEnabled,
                                        boolean isSalaryClient) {

        log.info("start of creating the loan offer. isInsuranceEnabled : {},  isSalaryClient: {}", isInsuranceEnabled, isSalaryClient);

        if (loanApplicationRequestDTO == null) {
            throw new RuntimeException("loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getAmount() == null) {
            throw new RuntimeException("amount in loanApplicationRequestDTO is null!");
        }
        if (loanApplicationRequestDTO.getTerm() == null) {
            throw new RuntimeException("term in loanApplicationRequestDTO is null!");
        }

        BigDecimal baseRate = new BigDecimal(Integer.parseInt(Objects.requireNonNull(env.getProperty("baseRate"))));

        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        loanOfferDTO.setApplicationId(++OFFERS_COUNT);
        loanOfferDTO.setRequestedAmount(loanApplicationRequestDTO.getAmount());
        loanOfferDTO.setTerm(loanApplicationRequestDTO.getTerm());
        loanOfferDTO.setIsInsuranceEnabled(isInsuranceEnabled);
        loanOfferDTO.setIsSalaryClient(isSalaryClient);

        BigDecimal rate = baseRate;
        BigDecimal creditAmount = loanApplicationRequestDTO.getAmount();

        if (isInsuranceEnabled) {
            rate = rate.subtract(new BigDecimal(3));

        }
        if (isSalaryClient) {
            rate = rate.subtract(new BigDecimal(1));
        }
        loanOfferDTO.setRate(rate);
        log.info("rate calculated as {}", rate);

        BigDecimal monthlyPayment = calcMonthlyPayment(rate, loanApplicationRequestDTO.getTerm(), creditAmount);
        log.info("monthlyPayment calculated as {}", monthlyPayment);
        loanOfferDTO.setMonthlyPayment(monthlyPayment);

        BigDecimal totalAmount = monthlyPayment.multiply(new BigDecimal(loanApplicationRequestDTO.getTerm()));
        log.info("totalAmount calculated as {}", totalAmount);
        loanOfferDTO.setTotalAmount(totalAmount);

        log.info("the loan offer created");
        return loanOfferDTO;
    }

    public static BigDecimal calcMonthlyPayment(BigDecimal rate, Integer term, BigDecimal totalAmount) {
        log.info("start of calculating monthlyRate in method calcMonthlyPayment");
        if (rate == null) {
            throw new RuntimeException("rate is null!");
        }
        if (totalAmount == null) {
            throw new RuntimeException("totalAmount is null!");
        }
        BigDecimal monthlyRate = rate.divide(new BigDecimal(100), 3, RoundingMode.HALF_UP).divide(new BigDecimal(12), 3, RoundingMode.HALF_UP);

        return totalAmount.multiply(monthlyRate).
                divide(new BigDecimal(1).subtract((new BigDecimal(1).add(monthlyRate)).pow(-term, MathContext.DECIMAL128)), 3, RoundingMode.HALF_UP);

    }
}
