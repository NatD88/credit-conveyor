package application.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ApplicationNotFoundException extends RuntimeException {
    private final String errorMessage;
}
