package techcompany.entities;

public class Response {
    public int errorCode;
    public String data;

    public Response() {
    }

    public Response(int errorCode, String data) {
        this.errorCode = errorCode;
        this.data = data;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getdata() {
        return data;
    }

    public void setdata(String data) {
        this.data = data;
    }
}
