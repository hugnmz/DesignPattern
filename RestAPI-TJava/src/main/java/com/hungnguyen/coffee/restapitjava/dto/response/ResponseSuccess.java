package com.hungnguyen.coffee.restapitjava.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;


// class này để chuẩn hóa response trả về cho client.
public class ResponseSuccess extends ResponseEntity<ResponseSuccess.Payload> {

    // mô tả data trả về cho các api PUT, PATCH,DELETE vì 3 method này trả về chỉ 1 string thoi.
    public ResponseSuccess(HttpStatusCode status, String message) {
        super(new Payload(status.value(), message),HttpStatus.OK); // trả về OK vì nếu trả về mã lỗi giống nahu thì
        // ko hiển thị dc data
    }

    // dành cho method GET, POST vì nó trả về thêm data nữa
    public ResponseSuccess(HttpStatusCode status, String message, Object data) {
        super(new Payload(status.value(), message, data), HttpStatus.OK);
    }

    public static class Payload{
        private final int status;
        private final String message;
        private Object data;

        public Payload(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public Payload(int status, String message, Object data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}
