package name.mrkandreev.ckpipeline.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ErrorMessageDto {
  private String type;

  private String message;
}
