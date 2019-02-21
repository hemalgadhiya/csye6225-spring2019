package io.webApp.springbootstarter.attachments;

public class metaData {
	private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;

    public metaData(String fileName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

	@Override
	public String toString() {
		return "[\"fileName\":\"" + fileName + ", \"fileDownloadUri\":\"" + fileDownloadUri + ", \"fileType\":\"" + fileType
				+ ", \"size\":\"" + size + "]";
	}
    
}
