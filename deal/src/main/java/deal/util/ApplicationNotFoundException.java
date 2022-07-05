package deal.util;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApplicationNotFoundException extends RuntimeException {
    private final Long applicationID;
}
