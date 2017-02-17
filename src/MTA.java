import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.smtp.*;

public class MTA {
	public static void main(String[] args)
	{
		String sender, recipient, subject, cc;
		String server = "smtp.rambler.ru";
		BufferedReader stdin;
		SMTPClient client;
		SimpleSMTPHeader header;
		Writer writer;
		
		stdin = new BufferedReader(new InputStreamReader(System.in));
		
		try
		{
			System.out.print("From: ");
	        System.out.flush();
	
	        sender = stdin.readLine();
	        
	        System.out.print("To: ");
            System.out.flush();

            recipient = stdin.readLine();

            System.out.print("Subject: ");
            System.out.flush();

            subject = stdin.readLine();

            header = new SimpleSMTPHeader(sender, recipient, subject);
            while (true)
            {
                System.out.print("CC <enter one address per line, hit enter to end>: ");
                System.out.flush();

                cc = stdin.readLine();

                if (cc== null || cc.length() == 0) {
                    break;
                }

                header.addCC(cc.trim());
            }
            
            client = new SMTPClient();
            client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

            client.connect(server);
            if (!SMTPReply.isPositiveCompletion(client.getReplyCode()))
            {
                client.disconnect();
                System.err.println("SMTP server refused connection.");
                System.exit(1);
            }
            
            client.login();
            client.helo(server);
            client.setSender(sender);
            client.addRecipient(recipient);

            writer = client.sendMessageData();
            
            if (writer != null)
            {
                writer.write(header.toString());
                writer.close();
                client.completePendingCommand();
            }

            client.logout();

            client.disconnect();
		} 
		catch (IOException ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
