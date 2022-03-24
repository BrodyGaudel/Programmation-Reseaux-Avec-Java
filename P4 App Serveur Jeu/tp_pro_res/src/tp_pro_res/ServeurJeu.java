package tp_pro_res;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


public class ServeurJeu extends Thread {
	
	private boolean isActive = true;
	private int nombreClients=0;
	private int nombreSecret;
	private boolean fin;
	private String gagnant;
	
	public static void main(String[] args) {
		
		new ServeurJeu().start();
		
	}
	public void run() {
		
		try {
			ServerSocket serverSocket = new ServerSocket(1234);
			nombreSecret = new Random().nextInt(1000); //genere un nombre entier aléatoire entre 0 et 1000
			System.out.println("Le serveur a choisit le nombre :"+nombreSecret);
			while(isActive) {
				Socket socket = serverSocket.accept();
				++nombreClients;
				new Conversation(socket, nombreClients).start();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}	
		
	}
	
	class Conversation extends Thread {
		
		private Socket socketClient;
		private int numero;
		public Conversation(Socket socketClient, int numero) {
			this.socketClient = socketClient;
			this.numero = numero;
		}
		
		public void run() {
			
			try {
				InputStream inputStream = socketClient.getInputStream();
				InputStreamReader isr = new InputStreamReader(inputStream);
				BufferedReader br = new BufferedReader(isr);
				PrintWriter pw = new PrintWriter(socketClient.getOutputStream(), true);
				String ipClient =  socketClient.getRemoteSocketAddress().toString();
				pw.println( "Bienvenue, vous etes le client numero "+numero+" , IP="+ipClient);
				System.out.println("Connexion du client numero "+numero+" , IP="+ipClient);
				pw.println("Devinez le nombre secret ... ?");
				while(true) {
					String req = br.readLine(); //lire la requette du client
					int nombre=0;
					boolean correctFormatRequest=false;
					try {
						nombre = Integer.parseInt(req);
						correctFormatRequest=true;
					}catch(NumberFormatException e) {
						correctFormatRequest=false;
				
					}
					if(correctFormatRequest) {
						System.out.println("Client "+ipClient+" Tentative avec le nombre :"+nombre);
						
						if(fin==false) {
							if(nombre>nombreSecret) {
								pw.println("Votre nombre est supérieur au nombre secret");
							}
							else if(nombre<nombreSecret) {
								pw.println("Votre nombre est inférieur au nombre secret");
							}
							else {
								pw.println("BRAVO, vous avez gegné");
								gagnant=ipClient;
								System.out.println("BRAVO au gagnant, IP Client :"+ipClient);
								fin = true;
							}
						}
						else {
							pw.println("Le jeux est terminé, le gagnant est :"+gagnant);
						}
					}
					else {
						pw.println("Format de nombre incorrect");
					}
					
					String reponse = "length = "+req.length();
					pw.println("reponse");
				}
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
		
	}

}




