package ceylon;

import org.eclipse.imp.runtime.PluginBase;
import org.osgi.framework.BundleContext;

public class CeylonPlugin extends PluginBase {

  public static final String kPluginID = "ceylon-ide";
  public static final String kLanguageID = "ceylon";

  /**
   * The unique instance of this plugin class
   */
  protected static CeylonPlugin sPlugin;

  public static CeylonPlugin getInstance() {
    if (sPlugin == null)
      new CeylonPlugin();
    return sPlugin;
  }

  public CeylonPlugin() {
    super();
    sPlugin = this;
  }

  public void start(BundleContext context) throws Exception {
    super.start(context);
  }

  @Override
  public String getID() {
    return kPluginID;
  }

  @Override
  public String getLanguageID() {
    return kLanguageID;
  }
}
