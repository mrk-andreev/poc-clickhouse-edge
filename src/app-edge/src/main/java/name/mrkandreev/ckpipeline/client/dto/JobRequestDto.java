package name.mrkandreev.ckpipeline.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobRequestDto {
  private boolean isResolved;

  private long createdAt;

  private String requestId;

  private String query;

  private String response;
}
