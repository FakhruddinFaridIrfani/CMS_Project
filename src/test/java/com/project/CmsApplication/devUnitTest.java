package com.project.CmsApplication;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.RunningText;
import com.project.CmsApplication.repository.RunningTextRepository;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class devUnitTest {

    @Autowired
    CmsServices cmsServices;

    @Autowired
    RunningTextRepository runningTextRepository;

@Test
    public void testGetRunningTextAndroid(){
    List<RunningText> runningTextList = new ArrayList<>();

    try {
        runningTextList = runningTextRepository.getRunningTextAndroid("0","0","0");
        String a  = "a";
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

}
