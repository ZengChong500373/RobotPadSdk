package hefu.pad.sdk.http;


import hefu.pad.sdk.utils.ByteUtil;
import hefu.pad.sdk.utils.CodeInstructionSet;
import hefu.pad.sdk.utils.SystemInfoUtil;

public class KeepHeartBeatThread extends Thread {
    @Override
    public void run() {
        RokidCmdSocket robotControlSocket= RokidCmdSocket.getInstance();
        String keepMsg = "0 "+ SystemInfoUtil.getMac()+" 4"+ ByteUtil.byteToHexStr(ByteUtil.intToByte(CodeInstructionSet.BUF_ACTION_HEARTBEAT), "");
        while (true){
            try
            {
                Thread.sleep(20000);        //keep live at least once per minute
                if (robotControlSocket.getReady()){
                    robotControlSocket.sendMsg(keepMsg);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
