package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.Alarm;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    private Socket utcSocket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
        try {
            this.utcSocket = new Socket("utcnist.colorado.edu", 13);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String checkStatus(Alarm alarm) {
        return "inactive\n";
        /*String time;
        BufferedReader bufferedReader;
        try {
            bufferedReader = Utilities.getReader(utcSocket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            time = bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Log.e(Constants.TAG, "[COMMUNICATION THREAD] Time is " + time + "\n");

        return "none";*/
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client");

            String client_id = socket.getInetAddress().getHostAddress();

            String message = bufferedReader.readLine();
            String op_type;
            String[] vars = message.split(",");
            op_type = vars[0];
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] command " + message);
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] optype " + op_type);

            HashMap<String, Alarm> data = serverThread.getData();
            Alarm alarm;

            if (op_type.equals("reset")) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Reset alarm for client " + client_id);
                data.remove(client_id);
            } else if (op_type.equals("poll")){
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Poll alarm for client " + client_id);
                String result = "none\n";
                if (data.containsKey(client_id)) {
                    alarm = data.get(client_id);
                    if (alarm.getStatus().equals("active\n")) {
                        result = "active\n";
                    } else {
                        result = checkStatus(alarm);
                        alarm.setStatus(result);
                        data.put(client_id, alarm);
                    }
                }

                printWriter.println(result);
                printWriter.flush();
            } else if (op_type.equals("set")) {
                String time = vars[1] + ":" + vars[2];
                data.put(client_id, new Alarm(time));
            } else {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client");
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
