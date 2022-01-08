import com.lambda.client.event.events.PacketEvent
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.threads.safeListener
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.util.EnumHand
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author mmvanheusden
 * @since 6/1/2022
 *
 * Code inspired by:
 * https://github.com/MHFNaN/Bowbombonlyclient-fixed/blob/main/src/main/java/me/alpha432/features/modules/misc/BowMcBomb.java
 * https://github.com/momentumdevelopment/cosmos/blob/main/src/main/java/cope/cosmos/client/features/modules/combat/FastProjectile.java
 * https://github.com/anOtherAnalyse/FamilyFunPack/blob/master/src/main/java/family_fun_pack/modules/BowBombModule.java
 */

internal object BowBombModule : PluginModule(
    name = "BowBomb",
    category = Category.COMBAT,
    description = "Increases projectile velocity on some servers",
    pluginMain = BowBombPluginMain
) {
    private val spoofs by setting("Spoofs", 10, 1..100, 10)
    private val others by setting("Others",true, description = "Enables BowBomb for eggs, snowballs and pearls.")
    private val bypass by setting("Bypass",false)
    init {
        safeListener<PacketEvent.Send> {
            if (it.packet is CPacketPlayerDigging) {
                if ((it.packet as CPacketPlayerDigging).action == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && (mc.player.getHeldItem(EnumHand.MAIN_HAND).item == Items.BOW)) {
                    connection.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING))
                    repeat(spoofs) {
                        val sin = -sin(Math.toRadians(mc.player.rotationYaw.toDouble()))
                        val cos = cos(Math.toRadians(mc.player.rotationYaw.toDouble()))
                        if (bypass) { //idk if this does anything special, i saw this being done in the first link
                            connection.sendPacket(CPacketPlayer.Position(mc.player.posX + (sin * 100), mc.player.posY + 5, mc.player.posZ + (cos * 100), false))
                            connection.sendPacket(CPacketPlayer.Position(mc.player.posX - (sin * 100), mc.player.posY, mc.player.posZ - (cos * 100), true))
                        } else {
                            connection.sendPacket(CPacketPlayer.Position(mc.player.posX - (sin * 100), mc.player.posY, mc.player.posZ - (cos * 100), true))
                            connection.sendPacket(CPacketPlayer.Position(mc.player.posX + (sin * 100), mc.player.posY + 5, mc.player.posZ + (cos * 100), false))
                        }
                    }
                }
            } else if (it.packet is CPacketPlayerTryUseItem && others) {
                if ((mc.player.getHeldItem(EnumHand.MAIN_HAND).item == Items.EGG) || (mc.player.getHeldItem(EnumHand.MAIN_HAND).item == Items.ENDER_PEARL) || (mc.player.getHeldItem(EnumHand.MAIN_HAND).item == Items.SNOWBALL)) {
                    repeat(spoofs) {
                        val sin = -sin(Math.toRadians(mc.player.rotationYaw.toDouble()))
                        val cos = cos(Math.toRadians(mc.player.rotationYaw.toDouble()))
                        if (bypass) { //idk if this does anything special, i saw this being done in the first link
                            connection.sendPacket(CPacketPlayer.Position(mc.player.posX + (sin * 100), mc.player.posY + 5, mc.player.posZ + (cos * 100), false))
                            connection.sendPacket(CPacketPlayer.Position(mc.player.posX - (sin * 100), mc.player.posY, mc.player.posZ - (cos * 100), true))
                        } else {
                            connection.sendPacket(CPacketPlayer.Position(mc.player.posX - (sin * 100), mc.player.posY, mc.player.posZ - (cos * 100), true))
                            connection.sendPacket(CPacketPlayer.Position(mc.player.posX + (sin * 100), mc.player.posY + 5, mc.player.posZ + (cos * 100), false))
                        }
                    }
                }
            } else return@safeListener
        }
    }
}