class KeyCrypterException extends Exception
{
    // Constructor that accepts a message
    public KeyCrypterException(String message, Exception err)
    {
        super(message, err);
    }
}
