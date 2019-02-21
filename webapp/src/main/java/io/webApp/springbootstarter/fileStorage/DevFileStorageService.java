package io.webApp.springbootstarter.fileStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
@Profile("dev")
public class DevFileStorageService implements FileStorageService {

	private AmazonS3 s3client;
	
	private Path fileStorageLocation;

	@Value("${amazonProperties.endpointUrl}")
	private String endpointUrl;
	@Value("${amazonProperties.bucketName}")
	private String bucketName;
	@Value("${amazonProperties.accessKey}")
	private String accessKey;
	@Value("${amazonProperties.secretKey}")
	private String secretKey;

	@PostConstruct
	private void initializeAmazon() {
//		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
//		this.s3client = new AmazonS3Client(credentials);
		
		this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new ProfileCredentialsProvider()).build();
	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;

	}

	private String generateFileName(MultipartFile multiPart) {
		return multiPart.getOriginalFilename();
	}

	private void uploadFileTos3bucket(String fileName, File file) {
		s3client.putObject(new PutObjectRequest(bucketName, fileName, file));
//				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
	}

	public String storeFile(MultipartFile multipartFile) {
		String fileUrl = "";
		try {
			File file = convertMultiPartToFile(multipartFile);
			String fileName = generateFileName(multipartFile);
			fileStorageLocation = Paths.get(endpointUrl + bucketName);
			fileUrl = fileStorageLocation + fileName;
			uploadFileTos3bucket(fileName, file);
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	public boolean DeleteFile(String fileUrl) {
		 try {
		        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
		        System.out.println("fileName : " + fileName);
		        s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
		        }
		        catch (AmazonServiceException e) {
		            // The call was transmitted successfully, but Amazon S3 couldn't process
		            // it, so it returned an error response.
		            e.printStackTrace();
		            return false;
		            } catch (SdkClientException e) {
		            // Amazon S3 couldn't be contacted for a response, or the client
		            // couldn't parse the response from Amazon S3.
		            e.printStackTrace();
		            return false;
		            }
		        return true;
	}

	public Path getFileStorageLocation() {
		return fileStorageLocation;
	}

}