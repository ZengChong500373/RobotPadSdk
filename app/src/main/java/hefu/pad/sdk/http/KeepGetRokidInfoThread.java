package hefu.pad.sdk.http;

public class KeepGetRokidInfoThread extends Thread {

    @Override
    public void run() {
        RokidCmdSocket robotControlSocket= RokidCmdSocket.getInstance();
        while (true){
            try
            {
                Thread.sleep(2000);
                if (robotControlSocket.getReady()){
                    robotControlSocket.getInfo();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
