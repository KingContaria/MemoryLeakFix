package ca.fxco.memoryleakfix.mixin.customPayloadLeak;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClient_freeBufferMixin {

    /*
     * Free the packets at the end of the tick
     */


    // Make sure that the there is a reference to release first!
    private boolean canRelease(ByteBuf buffer) {
        return buffer.refCnt() < 1 || buffer.release();
    }


    @Inject(
            method = "tick",
            at = @At("RETURN")
    )
    private void releaseAfterTick(CallbackInfo ci) {
        MemoryLeakFix.BUFFERS_TO_CLEAR.removeIf(this::canRelease);
    }
}
