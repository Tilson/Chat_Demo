import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
	
	private static int serverport = 3669;
    private static ServerSocket serverSocket;
    
    private static ArrayList<Socket> users = new ArrayList<Socket>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
    		serverSocket = new ServerSocket(serverport);
    		while (!serverSocket.isClosed()) {
    			waitNewUser();
    		}
    	} catch (IOException e) {
    		e.getStackTrace();
    	}
	}
	
	private static void waitNewUser() {
    	try {
    		Socket s = serverSocket.accept();
    		createNewUser(s);
    	} catch (IOException e) {
    		e.getStackTrace();
    	}
    }
	
	private static void createNewUser(final Socket socket) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					users.add(socket);
					
					BufferedReader mBuf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					while (socket.isConnected()) {
						String msg = mBuf.readLine();
						castChatMsg(msg);
					}
				} catch (IOException e) {
					e.getStackTrace();
				}
			}
			
		});
		t.start();
	}
	
	private static void castChatMsg(String Msg) {
		Socket[] allSocket = new Socket[users.size()];
		users.toArray(allSocket);
		
		for (Socket socket : allSocket) {
			try {
				System.out.print(Msg + "\n");
				BufferedWriter mBufWrite = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				mBufWrite.write(Msg + "\n");
				mBufWrite.flush();
			} catch (IOException e) {
				e.getStackTrace();
			}
		}
	}

}
