package org.avlasov;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) throws IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
//        File file1 = new File("/Users/artemvlasov/Downloads/15549833399421_france_F17_AMX_13_90_45_north_america.wotreplay");
//        File file1 = new File("./fileOutput.txt");
//        try (BufferedReader br =
//                new BufferedReader(
//                        new FileReader(
//                                file1))) {
//            String newLine;
//            PrintWriter writer = new PrintWriter("data_new.txt");
//            final StringBuilder stringBuilder = new StringBuilder();
//
//            while ((newLine = br.readLine()) != null) {
//                stringBuilder.append(newLine.replace(" ", ""));
//            }
//            String[] result = stringBuilder.toString().split(String.valueOf('\u0000'));
//            for (String s : result) {
//                if (s.contains("arenaUniqueID")) {
//
//                }
//            }
//            writer.print(stringBuilder.toString());
//            writer.flush();
//            int read;
//            while ((read = br.read()) != -1) {
//                String hexString = Integer.toHexString(read);
//                stringBuilder.append(String.format("int - %d, Hex - %s, Char - %s\n", read, hexString, (char) read));
//            }
//            writer.write(stringBuilder.toString());
//            writer.flush();
//        } catch (Exception e) {
//
//        }
//        File file = new File("./fileOutput.txt");
//        File data = new File("./data.json");
//        if (data.exists()) {
//            ObjectMapper wotApiObjectMapper = Jackson2ObjectMapperBuilder.json()
//                    .failOnUnknownProperties(false)
//                    .build();
//            JsonNode jsonNode = wotApiObjectMapper.readTree(data);
//            JsonNode matchResultJsonNode = jsonNode.get(0);
//            MatchResult matchResult = wotApiObjectMapper.readValue(matchResultJsonNode.traverse(), MatchResult.class);
//            System.out.println(matchResult);
//        }
//        RestTemplate wotApiRestTemplate = new RestTemplate();
//        ResponseEntity<String> forEntity = wotApiRestTemplate.getForEntity("http://wotreplays.ru/site/download/12100094", String.class);
//        PrintWriter writer = new PrintWriter("fileOutput.txt");
//                    writer.print(forEntity.getBody());
//            writer.flush();
//        PrintWriter writer = new PrintWriter("unencrypted.txt");
//        writer.print(decrypt());
//        writer.flush();
//        readReplayFileContent();
//        readReplayFileContent();
        readReplayFileContent();
    }

//    private static void decrypt(byte[] bytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
//        String key = "DE72BEA0DE04BEB1DEFEBEEFDEADBEEF";
//        byte[] keyBytes = new byte[key.length() / 2];
//        for (int i = 0; i < keyBytes.length; i++) {
//            int index = i * 2;
//            int j = Integer.parseInt(key.substring(index, index + 2), 16);
//            keyBytes[i] = (byte) j;
//        }
//        SecretKeySpec KS = new SecretKeySpec(keyBytes, "Blowfish");
//        Cipher cipher = Cipher.getInstance("Blowfish");
//        cipher.init(Cipher.DECRYPT_MODE, KS);
//        File file = new File("decrypted_content.txt");
//        byte[] previous = null;
//        Inflater inflater = new Inflater();
//        try (FileWriter fileWriter = new FileWriter(file)) {
//            int totalParts = bytes.length / 8;
//            if (bytes.length % 8 == 0) {
//                for (int i = 0; i < bytes.length; i += 8) {
//                    byte[] update = cipher.update(bytes, i, 8);
//                    if (previous != null && previous.length != 0) {
//                        for (int j = 0; j < update.length; j++) {
//                            update[j] = (byte) (update[j] ^ previous[j]);
//                        }
//                        previous = update;
//                        inflater.setInput(update, 0, 8);
//                        byte[] result = new byte[8];
//                        inflater.inflate(result);
//                        fileWriter.write(new String(update));
//                        fileWriter.flush();
//                    } else {
//                        previous = update;
//                    }
//                    int part = (i / 8) + 1;
//                    System.out.print(String.format("\rPart %d/%d (%d%%)", part, totalParts,
//                            (long) Math.floor(part == 0 ? 0 : ((double) part / totalParts) * 100)));
//                }
//
//                        inflater.end();
//                fileWriter.write(new String(cipher.update(new byte[8])));
//                System.out.println("\nDecrypted content saved. File path " + file.getAbsolutePath());
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    private static void readReplayFileContent() {
        try {
            byte[] bytes = Files.readAllBytes(new File("15549833399421_france_F17_AMX_13_90_45_north_america.wotreplay").toPath());
            bytes = readFirstPart(bytes);
            bytes = readSecondPart(bytes);
//            decrypt(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] readSecondPart(byte[] bytes) {
        String fileName = "second_part.json";
        File file = new File(fileName);
        byte[] leftBytes = readData(bytes, fileName);
        System.out.println("Second part saved. File path " + file.getAbsolutePath());
        return leftBytes;
    }

    private static byte[] readFirstPart(byte[] bytes) {
        String fileName = "first_part.json";
        File file = new File(fileName);
        byte[] leftBytes = readData(bytes, fileName);
        System.out.println("First part saved. File path " + file.getAbsolutePath());
        return leftBytes;
    }

    private static byte[] readData(byte[] bytes, String fileName) {
        byte openedParentheses = 0;
        boolean objectStart = false;
        boolean objectEnd = false;
        File file = new File(fileName);
        if (file.exists()) file.delete();
        try (FileWriter fileWriter = new FileWriter(file)) {
            for (int i = 0; i < bytes.length && !objectEnd; i++) {
                byte read = bytes[i];
                if (openedParentheses == 0 && (read == '{' || read == '[')) {
                    openedParentheses++;
                    objectStart = true;
                    fileWriter.write(read);
                    fileWriter.flush();
                } else {
                    if (objectStart && openedParentheses != 0) {
                        fileWriter.write(read);
                        fileWriter.flush();
                        if (read == '{' || read == '[') {
                            openedParentheses++;
                        }
                        if (read == '}' || read == ']') {
                            openedParentheses--;
                        }
                    }
                    if (objectStart && openedParentheses == 0) {
                        return Arrays.copyOfRange(bytes, i + 1, bytes.length);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new byte[]{};
    }

//    private static byte[] encrypt() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
//        String key = "DE72BEA0DE04BEB1DEFEBEEFDEADBEEF";
//        byte[] keyBytes = new byte[key.length() / 2];
//        for (int i = 0; i < keyBytes.length; i++) {
//            int index = i * 2;
//            int j = Integer.parseInt(key.substring(index, index + 2), 16);
//            keyBytes[i] = (byte) j;
//        }
//        SecretKeySpec KS = new SecretKeySpec(keyBytes, "Blowfish");
//        Cipher cipher = Cipher.getInstance("Blowfish");
//        cipher.init(Cipher.ENCRYPT_MODE, KS);
//        return cipher.doFinal("testtest2".getBytes());
//    }
//
//    private static void deencrypt() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
//        String key = "DE72BEA0DE04BEB1DEFEBEEFDEADBEEF";
//        byte[] keyBytes = new byte[key.length() / 2];
//        for (int i = 0; i < keyBytes.length; i++) {
//            int index = i * 2;
//            int j = Integer.parseInt(key.substring(index, index + 2), 16);
//            keyBytes[i] = (byte) j;
//        }
//        SecretKeySpec KS = new SecretKeySpec(keyBytes, "Blowfish");
//        Cipher cipher = Cipher.getInstance("Blowfish");
//        cipher.init(Cipher.DECRYPT_MODE, KS);
//        byte[] encrypt = encrypt();
//        byte[] bytes1 = Arrays.copyOfRange(encrypt, 0, 8);
//        byte[] bytes2 = Arrays.copyOfRange(encrypt, 8, encrypt.length);
//        System.out.println(new String(cipher.update(bytes1)));
//        System.out.println(new String(cipher.doFinal(bytes2)));
//    }

}
