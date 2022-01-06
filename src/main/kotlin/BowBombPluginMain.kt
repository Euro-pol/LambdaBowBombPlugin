import com.lambda.client.plugin.api.Plugin

internal object BowBombPluginMain: Plugin() {

    override fun onLoad() {
        modules.add(BowBombModule)
    }

    override fun onUnload() {
    }
}