package me.heartalborada.utils;

import me.heartalborada.config;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Jar 库加载器
 */
public class librariesLoader {
    private static final Logger logger = LogManager.getLogger("DependentLoader");

    private static void downloadFile(File file, URL url) throws IOException {
        try (InputStream is = url.openStream()) {
            Files.copy(is, file.toPath());
        }
    }

    /**
     * 从Maven仓库下载依赖
     *
     * @param groupId    组ID
     * @param artifactId 构建ID
     * @param version    版本
     * @param repo       仓库地址
     * @param extra      额外参数
     * @param file       保存文件
     * @param checkMD5   是否检查MD5
     * @return 下载成功返回true，否则返回false
     */
    static boolean downloadLibraryMaven(String groupId, String artifactId, String version, String extra, String repo, File file, boolean checkMD5) throws RuntimeException, IOException {
        // 创建文件夹
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            throw new RuntimeException("Failed to create " + file.getParentFile().getPath());

        // 下载地址格式
        if (!repo.endsWith("/")) repo += "/";
        repo += "%s/%s/%s/%s-%s%s.jar";
        String DownloadURL = String.format(repo, groupId.replace(".", "/"), artifactId, version, artifactId, version, extra); // 下载地址
        String FileName = artifactId + "-" + version + ".jar"; // 文件名

        // 检查MD5
        if (checkMD5) {
            File FileMD5 = new File(file.getParentFile(), FileName + ".md5");
            String DownloadMD5Url = DownloadURL + ".md5";
            URL DownloadMD5UrlFormat = new URL(DownloadMD5Url);

            if (FileMD5.exists() && !FileMD5.delete())
                throw new RuntimeException("Failed to delete " + FileMD5.getPath());

            downloadFile(FileMD5, DownloadMD5UrlFormat); // 下载MD5文件

            if (!FileMD5.exists()) throw new RuntimeException("Failed to download " + DownloadMD5Url);

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                boolean isSame = DigestUtils.md5Hex(fis).equals(new String(Files.readAllBytes(FileMD5.toPath()), StandardCharsets.UTF_8));
                if (!isSame) {
                    fis.close();
                    if (!file.delete()) throw new RuntimeException("Failed to delete " + file.getPath());
                }
            }
        } else if (file.exists() && !file.delete()) { // 不检查直接删原文件下新的
            throw new RuntimeException("Failed to delete " + file.getPath());
        }

        // 下载正式文件
        if (!file.exists()) {
            logger.info("Downloading " + DownloadURL);
            downloadFile(file, new URL(DownloadURL));
        }

        return file.exists();
    }

    /**
     * @param groupId    组ID
     * @param artifactId 构件ID
     * @param repoUrl    仓库地址
     * @param xmlTag     XML标签
     * @return 版本名
     */
    public static String getLibraryVersionMaven(String groupId, String artifactId, String repoUrl, String xmlTag) throws RuntimeException, IOException, ParserConfigurationException, SAXException {
        File CacheDir = new File(config.path, "cache");
        if (!CacheDir.exists() && !CacheDir.mkdirs())
            throw new RuntimeException("Failed to create " + CacheDir.getPath());
        String metaFileName = "maven-metadata-" + groupId + "." + artifactId + ".xml";
        File metaFile = new File(CacheDir, metaFileName);

        if (!repoUrl.endsWith("/")) repoUrl += "/";
        repoUrl += "%s/%s/"; // 根目录格式
        String repoFormat = String.format(repoUrl, groupId.replace(".", "/"), artifactId); // 格式化后的根目录

        // MD5
        File metaFileMD5 = new File(CacheDir, metaFileName + ".md5");
        if (metaFileMD5.exists() && !metaFileMD5.delete())
            throw new RuntimeException("Failed to delete " + metaFileMD5.getPath());

        URL metaFileMD5Url = new URL(repoFormat + "maven-metadata.xml.md5");

        downloadFile(metaFileMD5, metaFileMD5Url);

        if (!metaFileMD5.exists()) throw new RuntimeException("Failed to download " + metaFileMD5Url);

        // 验证meta文件
        logger.info("Verifying " + metaFileName);
        if (metaFile.exists()) {
            try (FileInputStream fis = new FileInputStream(metaFile)) {
                if (!DigestUtils.md5Hex(fis).equals(new String(Files.readAllBytes(metaFileMD5.toPath()), StandardCharsets.UTF_8))) {
                    fis.close();
                    if (!metaFile.delete()) throw new RuntimeException("Failed to delete " + metaFile.getPath());

                    URL metaFileUrl = new URL(repoFormat + "maven-metadata.xml");
                    downloadFile(metaFile, metaFileUrl);
                    if (!metaFileMD5.exists()) throw new RuntimeException("Failed to download " + metaFileUrl);
                }
            }
        } else {
            URL metaFileUrl = new URL(repoFormat + "maven-metadata.xml");
            logger.info("Downloading " + metaFileUrl);
            downloadFile(metaFile, metaFileUrl);
            if (!metaFileMD5.exists()) throw new RuntimeException("Failed to download " + metaFileUrl);
        }

        // 读取内容
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(metaFile);
        return doc.getElementsByTagName(xmlTag).item(0).getFirstChild().getNodeValue();
    }

    /**
     * 加载 Maven 仓库的依赖库
     *
     * @param groupId    组ID
     * @param artifactId 构件ID
     * @param version    版本
     * @param repo       仓库地址
     * @param extra      额外参数
     * @param path       保存目录
     */
    public static void loadLibraryClassMaven(String groupId, String artifactId, String version, String extra, String repo, File path) throws RuntimeException, IOException {
        String name = artifactId + "-" + version + ".jar"; // 文件名

        // jar
        File saveLocation = new File(path, name);
        logger.info("Verifying " + name);
        if (!downloadLibraryMaven(groupId, artifactId, version, extra, repo, saveLocation, true)) {
            throw new RuntimeException("Failed to download libraries!");
        }

        // -- 加载开始 --
        loadLibraryClassLocal(saveLocation);
    }

    /**
     * 加载本地 Jar
     *
     * @param file Jar 文件
     */
    static void loadLibraryClassLocal(File file) throws IOException {
        logger.info("Loading library " + file);
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL url = file.toURI().toURL();
        if (classLoader instanceof URLClassLoader) {
            //Java 8
            URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class<URLClassLoader> sysClass = URLClassLoader.class;
            try {
                Method method = sysClass.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(sysLoader, url);
            } catch (Exception var5) {
                var5.printStackTrace();
                throw new IllegalStateException(var5.getMessage(), var5);
            }
        } else {
            try {
                Field field;
                try {
                    // Java 9 - 15
                    field = classLoader.getClass().getDeclaredField("ucp");
                } catch (NoSuchFieldException e) {
                    // Java 16+
                    field = classLoader.getClass().getSuperclass().getDeclaredField("ucp");
                }
                field.setAccessible(true);
                Object ucp = field.get(classLoader);
                Method method = ucp.getClass().getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(ucp, url);
            } catch (Exception exception) {
                exception.printStackTrace();
                throw new IllegalStateException(exception.getMessage(), exception);
            }
        }
    }
}