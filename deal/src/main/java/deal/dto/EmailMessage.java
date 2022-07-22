package deal.dto;

import deal.util.ThemeEmail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class EmailMessage {
    private String address;
    private ThemeEmail themeEmail;
    private Long applicationID;
}
