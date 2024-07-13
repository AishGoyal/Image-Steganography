import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.crypto.NoSuchPaddingException;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

class Pixel{
    private int x;
    private int y;
    private Color color;
    
    Pixel(int x, int y, Color color){
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public Color getColor(){
        return this.color;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }
}

class imageSteganography extends JFrame implements ActionListener{
    JButton b1 = new JButton("Encrypt");
    JButton b2 = new JButton("Decrypt");
    JLabel l1 = new JLabel("Message:");
    JButton b4 = new JButton("file");
    JTextField t1 = new JTextField(20);

    imageSteganography() throws NoSuchAlgorithmException, NoSuchPaddingException{
        setTitle("Image Steganography");
        t1.setBounds(30, 40, 225, 60); 
        // xy cordinates and height aur width
        add(t1);
        l1.setBounds(30, 10, 200, 30);
        add(l1);
        b2.setBounds(140, 120, 85, 30);
        add(b2);
        b1.setBounds(50, 120, 85, 30);
        add(b1);
        add(b4);
        setVisible(true);
        // visiblity
        setSize(300, 220);
        //  new window size
        setLayout(null);
        b1.addActionListener(this);
        b2.addActionListener(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // cross kaam na krta agr ni hota to
    }

    public void actionPerformed(ActionEvent e){
        JFileChooser fc = new JFileChooser();
        fc.showOpenDialog(null);
        File file = fc.getSelectedFile();
        boolean isImage = isFileImage(file);

        if(!isImage){
            JOptionPane.showMessageDialog(null, "Please select an image file");
            return;
        }

        if(e.getSource() == b1){
            String message = t1.getText();
            if(message.length() == 0){
                JOptionPane.showMessageDialog(null, "Please enter a message");
                return;
            }

            try{
                imageSteganography.encrypt(file, message);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(null, "Error in encryption");
            }
        }

        else if(e.getSource() == b2){
            try{
                String message = imageSteganography.decrypt(file);
                JOptionPane.showMessageDialog(null, "Message: " + message);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(null, "Error in decryption");
            }
        }
    }

    public static void encrypt(File file, String message) throws Exception{
        BufferedImage img = ImageIO.read(file);

        try{
            img = ImageIO.read(file);
            BufferedImage imgToEncrypt = getImageToEncrypt(img, message);
            // rgb values leaati h colormodel lib
            Pixel[] pixelArray = getPixelArray(imgToEncrypt);
            String[] binaryMessage = convertMessageToBinary(message);
            encodeMessageBinaryInPixels(pixelArray, binaryMessage);
            // 3 pixels hold a character
            replacePixelsInNewBufferedImage(imgToEncrypt, pixelArray);
            saveNewImage(imgToEncrypt, file.getPath());
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error in reading image");
        }
        JOptionPane.showMessageDialog(null, "Encrypted");
    }

    public static BufferedImage getImageToEncrypt(BufferedImage image, String msg){
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        BufferedImage img = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        return img;
    }

    private static Pixel[] getPixelArray(BufferedImage imgToEncrypt){
        int width = imgToEncrypt.getWidth();
        int height = imgToEncrypt.getHeight();
        Pixel[] pixelArray = new Pixel[height * width];
        int count = 0;

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                pixelArray[count++] = new Pixel(i, j, new Color(imgToEncrypt.getRGB(i, j)));
                // location ,x nd y cordinate 
            }
        }

        return pixelArray;
    }

    private static String[] convertMessageToBinary(String message){
        int[] messageAscii = convertMessageToAscii(message);
        String[] binaryMessage = convertAsciiToBinary(messageAscii);
        return binaryMessage;
    }

    private static int[] convertMessageToAscii(String message){
        int[] messageAscii = new int[message.length()];

        for(int i = 0; i < message.length(); i++){
            messageAscii[i] = (int)message.charAt(i);
        }
        return messageAscii;
    }

    private static String[] convertAsciiToBinary(int[] messageAscii){
        String[] binaryMessage = new String[messageAscii.length];

        for(int i = 0; i < messageAscii.length; i++){
            String binary = leftPadZeros(Integer.toBinaryString(messageAscii[i]));
            binaryMessage[i] += binary;
        }
        return binaryMessage;
    }

    private static String leftPadZeros(String binaryMessage){
        int length = binaryMessage.length();
        int padLength = 8 - length % 8;

        for(int i = 0; i < padLength; i++){
            binaryMessage = "0" + binaryMessage;
        }
        return binaryMessage;
    }

    private static void encodeMessageBinaryInPixels(Pixel pixelArray[], String[] binaryMessage){
        int pixelIndex = 0;
        boolean isLastChar = false;

        for(int i = 0; i < binaryMessage.length; i++){
            Pixel[] currentPixels = {pixelArray[pixelIndex], pixelArray[pixelIndex + 1], pixelArray[pixelIndex + 2]};
            if(i + 1 == binaryMessage.length){
                isLastChar = true;
            }
            changePixelColors(currentPixels, binaryMessage[i], isLastChar);
            pixelIndex += 3;
        }
    }

    private static void changePixelColors(Pixel[] pixels, String binaryMessage, boolean isLastChar){
        int messageIndex = 0;
        for(int i = 0; i < pixels.length - 1; i++){
            char[] messageBinaryChar = {binaryMessage.charAt(messageIndex), binaryMessage.charAt(messageIndex + 1), binaryMessage.charAt(messageIndex + 2)};
            String[] pixelRGBBinary = getPixelRGBBinary(pixels[i], messageBinaryChar);
            pixels[i].setColor(getnewPixelColor(pixelRGBBinary));
            messageIndex += 3;
        }
        if(isLastChar == false){
            char[] messageBinaryChar = {binaryMessage.charAt(messageIndex), binaryMessage.charAt(messageIndex + 1), '1'};
            String[] pixelRGBBinary = getPixelRGBBinary(pixels[2], messageBinaryChar);
            pixels[2].setColor(getnewPixelColor(pixelRGBBinary));
        }
        else{
            char[] messageBinaryChar = {binaryMessage.charAt(messageIndex), binaryMessage.charAt(messageIndex + 1), '0'};
            String[] pixelRGBBinary = getPixelRGBBinary(pixels[2], messageBinaryChar);
            pixels[2].setColor(getnewPixelColor(pixelRGBBinary));
        }
    }

    private static String[] getPixelRGBBinary(Pixel pix, char[] messageBinaryChar){
        String[] pixelRGBBinary = new String[3];
        pixelRGBBinary[0] = changePixelBinary(Integer.toBinaryString(pix.getColor().getRed()), messageBinaryChar[0]);
        pixelRGBBinary[1] = changePixelBinary(Integer.toBinaryString(pix.getColor().getGreen()), messageBinaryChar[1]);
        pixelRGBBinary[2] = changePixelBinary(Integer.toBinaryString(pix.getColor().getBlue()), messageBinaryChar[2]);
        return pixelRGBBinary;
    }

    private static String changePixelBinary(String pixelBinary, char messageBinaryChar){
        StringBuilder sb = new StringBuilder(pixelBinary);
        sb.setCharAt(pixelBinary.length() - 1, pixelBinary.charAt(pixelBinary.length() - 1));
        return sb.toString();
    }

    private static Color getnewPixelColor(String[] pixelRGBBinary){
        int red = Integer.parseInt(pixelRGBBinary[0], 2);
        int green = Integer.parseInt(pixelRGBBinary[1], 2);
        int blue = Integer.parseInt(pixelRGBBinary[2], 2);
        return new Color(red, green, blue);
    }

    private static void replacePixelsInNewBufferedImage(BufferedImage imgToEncrypt, Pixel[] pixelArray){
        for(int i = 0; i < pixelArray.length; i++){
            imgToEncrypt.setRGB(pixelArray[i].getX(), pixelArray[i].getY(), pixelArray[i].getColor().getRGB());
        }
    }

    private static void saveNewImage(BufferedImage imgToEncrypt, String path){
        try{
            File outputfile = new File(path);
            ImageIO.write(imgToEncrypt, "png", outputfile);
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error in saving image");
        }
    }

    public static String decrypt(File file) throws Exception{
        try{
            BufferedImage img = ImageIO.read(file);  
            Pixel[] pixelArray = getPixelArray(img);
            String msg = decodeMessageFromPixels(pixelArray);
            return msg;
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error in reading image");
        }
        return "";
    }

    private static String decodeMessageFromPixels(Pixel[] pixelArray){
        boolean completed = false;
        int pixelIndex = 0;
        StringBuilder message = new StringBuilder("");
        while(!completed){
            Pixel[] pixelPairs = new Pixel[3];
            for(int i = 0; i < 3; i++){
                pixelPairs[i] = pixelArray[pixelIndex++];
            }
            message.append(convertPixelsToChar(pixelPairs));
            if(isEndOfMessage(pixelPairs[2])){
                completed = true;
            }
        }
        return message.toString();
    }

    private static char convertPixelsToChar(Pixel[] pixelPairs){
        ArrayList<String> binaryMessageList = new ArrayList<String>();
        for(int i = 0; i < pixelPairs.length; i ++){
            String[] currentBinary = convertIntToBinary(pixelPairs[i]);
            binaryMessageList.add(currentBinary[0]);
            binaryMessageList.add(currentBinary[1]);
            binaryMessageList.add(currentBinary[2]);
        }
        return convertBinaryValuesToChar(binaryMessageList);
    }

    private static String[] convertIntToBinary(Pixel pix){
        String[] pixelRGBBinary = new String[3];
        pixelRGBBinary[0] = Integer.toBinaryString(pix.getColor().getRed());
        pixelRGBBinary[1] = Integer.toBinaryString(pix.getColor().getGreen());
        pixelRGBBinary[2] = Integer.toBinaryString(pix.getColor().getBlue());
        return pixelRGBBinary;
    }

    private static char convertBinaryValuesToChar(ArrayList<String> binaryMessageList){
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < binaryMessageList.size()-1; i++){
            sb.append(binaryMessageList.get(i).charAt(binaryMessageList.get(i).length() - 1));
        }
        String noZeros = removePaddedZeros(sb.toString());
        int ascii = Integer.parseInt(noZeros, 2);
        return (char)ascii;
    }

    private static String removePaddedZeros(String s){
        int index = 0;
        while(s.charAt(index) == '0'){
            index++;
        }
        return s.substring(index, s.length());
    }

    private static boolean isEndOfMessage(Pixel pix){
        String[] pixelRGBBinary = convertIntToBinary(pix);
        if(pixelRGBBinary[2].charAt(pixelRGBBinary[2].length() - 1) == '0'){
            return true;
        }
        return false;
    }

    public static boolean isFileImage(File file) {
        try {
            return ImageIO.read(file) != null;
        } catch (IOException e) {
            return false;
        }
    }
}

public class imageSteg{
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException{
        new imageSteganography();
    }
}