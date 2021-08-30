package name.mrkandreev.ckpipeline.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobDto {
  private boolean isResolved;

  private long createdAt;

  private String requestId;

  private String query;

  private String response;
}
