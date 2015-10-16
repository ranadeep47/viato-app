package in.viato.app.http.clients;

import com.squareup.moshi.Moshi;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit.Converter;

public final class ToStringConverterFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
        //noinspection EqualsBetweenInconvertibleTypes
        if (String.class.equals(type)) {
            return new Converter<ResponseBody, Object>() {

                @Override
                public Object convert(ResponseBody responseBody) throws IOException {
                    return responseBody.string();
                }
            };
        }

        return null;
    }

    @Override
    public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
        //noinspection EqualsBetweenInconvertibleTypes
        if (String.class.equals(type)) {
            return new Converter<String, RequestBody>() {

                @Override
                public RequestBody convert(String value) throws IOException {
                    return RequestBody.create(MediaType.parse("text/plain"), value);
                }
            };
        }

        return null;
    }
}