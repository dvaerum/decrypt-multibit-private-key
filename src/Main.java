import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws KeyCrypterException, IOException {
        KeyCrypterOpenSSL key_handler = new KeyCrypterOpenSSL();

        String encrypted_private_key_base64 = "";
        if ( encrypted_private_key_base64.length() == 0 ) {
            System.out.println("Enter the encrypted private key (format: Base64 - Note: no newlines):");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
            encrypted_private_key_base64 = buffer.readLine();
            System.out.println();
            System.out.println();
        }

        CharSequence private_key_password = "";
        if ( private_key_password.length() == 0 ) {
            System.out.println("Enter the private key password (Note: no newlines):");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
            private_key_password = buffer.readLine();
            System.out.println();
            System.out.println();
        }

        String private_key_base64 = key_handler.decrypt(encrypted_private_key_base64, private_key_password);
        System.err.println();
        System.out.printf("Private Key (Base64): %s\n\n", private_key_base64);
    }
}
