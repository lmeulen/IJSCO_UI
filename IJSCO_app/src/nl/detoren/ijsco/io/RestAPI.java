package nl.detoren.ijsco.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class RestAPI{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("API Example");
            JButton button = new JButton("Fetch Data");

            button.addActionListener(e -> {
				SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                    @Override
                    protected Void doInBackground() throws Exception {
            			System.out.println("Button pressed");
                        //URL url = new URL("https://api.schaakrating.nl");
                        URL url = new URL("https://api.schaakrating.nl/private-key-request");
            			System.out.println("Set url");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
            			System.out.println("Request methode set");
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            			System.out.println("Bufferreader");
                        String inputLine;
                        StringBuilder content = new StringBuilder();
            			System.out.println("Starting content");
                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine);
                        }
                        in.close();
            			System.out.println("Input closed");
                        publish(content.toString());
                        return null;
                    }

                    @Override
                    protected void process(List<String> chunks) {
                        String data = chunks.get(chunks.size() - 1);
                        System.out.println(data); // Handle the data received
                    }
                };
                worker.execute();
            });

            frame.add(button);
			System.out.println("Added button");
            frame.setSize(300, 100);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
