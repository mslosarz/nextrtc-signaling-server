package org.nextrtc.signalingserver.property;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManualNextRTCProperties implements NextRTCProperties {
    @Builder.Default
    private int maxConnectionSetupTime = 30;
    @Builder.Default
    private int pingPeriod = 3;
    @Builder.Default
    private int schedulerPoolSize = 10;
    @Builder.Default
    private boolean joinOnlyToExisting = false;
    @Builder.Default
    private String defaultConversationType = "MESH";
}
