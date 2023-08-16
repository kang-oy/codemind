package com.example.codemind.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * todo
 *
 * @Author ouyangkang
 * @date 2023/8/15
 */

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
@Api(value = "chat 接口", tags = {"chat 接口"})
public class ChatGPTController {

    private final ChatGPT chatGPT;


    @ApiOperation(value = "获取需求信息目录")
    @ApiOperationSupport(author = "ouyangkang", order = 1)
    @GetMapping("/getPrdInfo")
    public ResultVo<JSONObject> getPrdInfo(@RequestParam String content,
                                           @RequestParam(value = "fake", required = false) @ApiParam(value = "是否需要假数据", defaultValue = "false") Boolean fake) {
        ResultVo<JSONObject> resultVo = new ResultVo<>();
        try {
            if (fake != null && fake) {
                throw new RuntimeException("fake");
            }
            log.info("getPrdInfo content:{}", content);
            Long start = System.currentTimeMillis();
            Message system = Message.ofSystem("你现在是一个资深前端工程师，可以帮助前端建立 react相关的文件名，备注，明确清晰，每个文件都要有对应的备注，以及开发估时人天为单位。");
            Message message = Message.of("生成一个" + content + "的需求，建立一个前端相关的文件目录， 目录结构要求以JSONObject数据格式返回。JSONObject数据格式参考如下：\n {\"name\":\"xxx.tsx\",\"type\":\"file\",\"description\":\"组件描述\",\"time\":1} name 为文件名，type为文件类型，description为文件描述，time为开发估时人天为单位");
            ChatCompletion chatCompletion = getChatCompletion(system, message);
            ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
            log.info("getPrdInfo cost:{}", System.currentTimeMillis() - start);
            Message res = response.getChoices().get(0).getMessage();
            System.out.println(res);
            return resultVo.success(JSON.parseObject(getJSON(res.getContent())));
        } catch (Exception e) {
            log.error("getPrdInfo error", e);
            return resultVo.success(getPrdInfoPocketBottom(content));
        }
    }

    private JSONObject getPrdInfoPocketBottom(String content) {

        String res = "{\"children\":[{\"children\":[{\"children\":[{\"name\":\"ImageGenerator.tsx\",\"description\":\"%s组件\",\"time\":3,\"type\":\"file\"}],\"name\":\"components\",\"description\":\"组件文件夹\",\"type\":\"folder\"},{\"children\":[{\"name\":\"imageUtils.ts\",\"description\":\"%s工具函数\",\"time\":2,\"type\":\"file\"}],\"name\":\"utils\",\"description\":\"工具函数文件夹\",\"type\":\"folder\"},{\"name\":\"App.tsx\",\"description\":\"应用主入口文件\",\"time\":1,\"type\":\"file\"}],\"name\":\"src\",\"description\":\"源代码文件夹\",\"type\":\"folder\"},{\"children\":[{\"name\":\"index.html\",\"description\":\"主HTML文件\",\"time\":0.5,\"type\":\"file\"}],\"name\":\"public\",\"description\":\"公共资源文件夹\",\"type\":\"folder\"},{\"name\":\"package.json\",\"description\":\"项目配置文件\",\"time\":0.5,\"type\":\"file\"}],\"name\":\"auto-generate-images\",\"description\":\"%s的项目文件夹\",\"time\":7,\"type\":\"folder\"}";
        // 替换res中的%s 为content
        String format = String.format(res, content, content, content);
        return JSON.parseObject(format);
    }


    @ApiOperation("获取细化需求信息目录")
    @ApiOperationSupport(author = "ouyangkang", order = 2)
    @GetMapping("/getNextPrdInfo")
    public ResultVo<JSONObject> getNextPrdInfo(@RequestParam String content, @RequestParam Integer time, @RequestParam(value = "fake", required = false) @ApiParam(value = "是否需要假数据", defaultValue = "false") Boolean fake) {
        ResultVo<JSONObject> resultVo = new ResultVo<>();

        try {
            if (fake != null && fake) {
                throw new RuntimeException("fake");
            }
            log.info("getNextPrdInfo content:{}, time:{}", content, time);
            Long start = System.currentTimeMillis();
            Message system = Message.ofSystem("你现在是一个资深前端工程师，可以帮助前端细化组件模块相关的文件名，备注，明确清晰，每个文件都要有对应的备注，以及开发估时人天为单位。");
            Message message = Message.of("对" + content + "进行建立一个前端相关的文件目录并进行估时， 目录结构要求以JSONObject数据格式返回。JSONObject数据格式如下：\n {\"name\":\"xxx.tsx\",\"type\":\"file\",\"description\":\"组件描述\",\"time\":1} name 为文件名，type为文件类型，description为文件描述，time为开发估时人天为单位。需要对JSON数据进行验证，并且所有time的大小相加不超过" + time);
            ChatCompletion chatCompletion = getChatCompletion(system, message);
            ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
            log.info("getNextPrdInfo cost:{}", System.currentTimeMillis() - start);
            Message res = response.getChoices().get(0).getMessage();
            System.out.println(res);
            return resultVo.success(JSON.parseObject(getJSON(res.getContent())));
        } catch (Exception e) {
            log.error("getNextPrdInfo error", e);
            return resultVo.success(getNextPrdInfoPocketBottom(content, time));
        }
    }

    private JSONObject getNextPrdInfoPocketBottom(String content, Integer time) {
        String res = "{\"name\":\"ImageGenerator\",\"description\":\"%s组件\",\"time\":%d,\"type\":\"folder\",\"children\":[{\"name\":\"ImageGenerator.tsx\",\"description\":\"%s主组件\",\"time\":%.1f,\"type\":\"file\"},{\"name\":\"ImageGeneratorStyles.ts\",\"description\":\"%s样式文件\",\"time\":%.1f,\"type\":\"file\"},{\"name\":\"ImageGeneratorProps.ts\",\"description\":\"%s属性类型定义文件\",\"time\":%.1f,\"type\":\"file\"},{\"name\":\"ImageGeneratorTest.tsx\",\"description\":\"%s测试文件\",\"time\":%.1f,\"type\":\"file\"},{\"name\":\"ImageGeneratorStories.tsx\",\"description\":\"%s故事书文件，用于展示组件的各种使用场景\",\"time\":%.1f,\"type\":\"file\"}]}";
        // 替换res中的所有%s 为content 。第一个 %f 为 time 。 其余的 %f的总和为 time
        // 将time随机分成5份
        double[] times = new double[5];
        double sum = 0;
        for (int i = 0; i < 4; i++) {
            times[i] = new BigDecimal(Math.random() * time / 4).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            sum += times[i];
        }
        times[4] = time - sum;
        String format = String.format(res, content, time, content, times[0], content, times[1], content, times[2], content, times[3], content, times[4]);
        return JSON.parseObject(format);

    }


    @ApiOperation("获取需求帮助信息")
    @ApiOperationSupport(author = "ouyangkang", order = 3)
    @GetMapping("/getCodeHelp")
    public ResultVo<JSONObject> getCodeHelp(@RequestParam String content, @RequestParam(value = "fake", required = false) @ApiParam(value = "是否需要假数据", defaultValue = "false") Boolean fake) {
        ResultVo<JSONObject> resultVo = new ResultVo<>();
        JSONObject json = new JSONObject();
        try {
            if (fake != null && fake) {
                throw new RuntimeException("fake");
            }
            log.info("getCodeHelp content:{}", content);
            Long start = System.currentTimeMillis();
            Message system = Message.ofSystem("你现在是一个资深前端工程师，可以提供相应的建议");
            Message message = Message.of("请围绕" + content + "提供帮助， 要求：1.请提供帮助性的函数和注释和代码实现 2.todolist");
            ChatCompletion chatCompletion = getChatCompletion(system, message);
            ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
            log.info("getCodeHelp cost:{}", System.currentTimeMillis() - start);
            Message res = response.getChoices().get(0).getMessage();
            System.out.println(res);
            json.put("suggest", getJavascript(res.getContent()));
            json.put("todoList", getSendJavascript(res.getContent()));
            return resultVo.success(json);
        } catch (Exception e) {
            log.error("getCodeHelp error", e);
            json.put("suggest", "暂无建议");
            json.put("todoList", "暂无todoList");
            return resultVo.success(json);
        }
    }

    private ChatCompletion getChatCompletion(Message system, Message message) {
        return ChatCompletion.builder()
                .model(ChatCompletion.Model.GPT_4.getName())
                .messages(Arrays.asList(system, message))
                .maxTokens(3000)
                .temperature(0.2)
                .build();
    }

    private String getJSON(String res) {
        // 获取字符串中第一个{ 和 最后一个} 之间的内容
        return res.substring(res.indexOf("{"), res.lastIndexOf("}") + 1);
    }

    private String getJavascript(String res) {
        // 获取字符串中 第一个被```javascript ```包裹的内容
        return res.substring(res.indexOf("```javascript") + 13, res.lastIndexOf("```"));
    }

    private String getSendJavascript(String res) {
        // 获取字符串中 第二个被```javascript ```包裹的内容
        return res.substring(res.indexOf("```javascript", res.indexOf("```javascript") + 1) + 13, res.lastIndexOf("```"));
    }

}
