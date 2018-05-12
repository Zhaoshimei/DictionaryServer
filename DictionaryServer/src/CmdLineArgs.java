/**
 * Created by shimei on 2017/8/30.
 */
import org.dictionary.args4j.Option;
public class CmdLineArgs {

    @Option(required = true, name = "-h", aliases = {"--host"}, usage = "Hostname")
    private String host;

    @Option(required = false, name = "-p", usage = "Port number")
    private int port = 4444;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }


}
