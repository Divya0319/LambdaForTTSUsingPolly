package models;

public class Response {
    private Status status;
    private String fileUrl;

    public Response(Status status, String fileUrl) {
        this.status = status;
        this.fileUrl = fileUrl;
    }

    public Response() {

    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
