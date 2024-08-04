package com.cozymate.cozymate_server.global.s3.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class S3RequestDto {

    List<String> fileNames;

}
