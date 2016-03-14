package com.taobao.luaview.debug;

import android.os.Build;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by lamo on 16/2/16.
 */
public class DebugConnection extends Socket {

    private static final String EMULATOR_LOCALHOST = "10.0.2.2";
    private static final String GENYMOTION_LOCALHOST = "10.0.3.2";
    private static final String DEVICE_LOCALHOST = "localhost";
    private static final int PORT = 9876;

    private DataInputStream input = null;
    private DataOutputStream output = null;

    public boolean sendingEnabled = true;

    private static String getDebugServerHost() {
        // Since genymotion runs in vbox it use different hostname to refer to adb host.
        // We detect whether app runs on genymotion and replace js bundle server hostname accordingly
        if (isRunningOnGenymotion()) {
            return GENYMOTION_LOCALHOST;
        }
        if (isRunningOnStockEmulator()) {
            return EMULATOR_LOCALHOST;
        }

        return DEVICE_LOCALHOST;
    }

    private static boolean isRunningOnGenymotion() {
        return Build.FINGERPRINT.contains("vbox");
    }

    private static boolean isRunningOnStockEmulator() {
        return Build.FINGERPRINT.contains("generic");
    }

    public static DebugConnection create() {
        String host = getDebugServerHost();
        try {
            return new DebugConnection(host, PORT);
        } catch (IOException e) {
            return null;
        }
    }

    public DebugConnection(String host, int port) throws IOException {
        super(host, port);
        input = new DataInputStream(getInputStream());
        output = new DataOutputStream(getOutputStream());
    }

    public boolean sendScript(byte[] content, String fileName) {
        return sendCmd("loadfile", fileName, content);
    }

    public boolean sendCmd(String cmd, String fileName, String info) {
        return sendCmd(cmd, fileName, info != null ? info.getBytes() : null);
    }

    public boolean sendCmd(String cmd, String fileName, byte[] info) {
        String header = "Cmd-Name:" + cmd + "\n";
        if (fileName != null) {
            header += "File-Name:" + fileName + "\n";
        }
        header += "\n";

        if (info == null) {
            return sendBytes(header.getBytes());
        } else {
            return sendBytes(header.getBytes(), info);
        }
    }

    public boolean sendBytes(byte[]... pieces) {
        if (!sendingEnabled) {
            return false;
        }
        try {
            int length = 0;
            for (byte[] bytes: pieces) {
                length += bytes.length;
            }

            output.writeByte(length >> 24);
            output.writeByte(length >> 16);
            output.writeByte(length >> 8);
            output.writeByte(length);

            for (byte[] bytes: pieces) {
                output.write(bytes);
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String reciveCMD() {
        try {
            int length = 0;
            length |= input.readByte() << 24;
            length |= input.readByte() << 16;
            length |= input.readByte() << 8;
            length |= input.readByte();

            byte[] buffer = new byte[length];
            input.read(buffer);

            return new String(buffer);
        } catch (IOException e) {
            return null;
        }
    }

}
