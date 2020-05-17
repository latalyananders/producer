package RabbitMQ;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class ApplicationController {

    Logger logger = Logger.getLogger(ApplicationController.class);

    @Autowired
    RabbitTemplate template;

    @RequestMapping("/")
    ModelAndView home() {
        return new ModelAndView("main");
    }

    @RequestMapping("/result")
    @ResponseBody
    public ModelAndView  getSummary(
            @RequestParam(value="existence",required=true) String existence,
            @RequestParam(value="extension",required=true) String extension,
            @RequestParam(value="sequence1",required=true) String sequence1,
            @RequestParam(value="sequence2",required=true) String sequence2
    ) {
        JSONObject json = new JSONObject();
        ModelAndView mav = new ModelAndView("result");
        try {
            json.put("existence", existence);
            json.put("extension", extension);
            json.put("sequence1", sequence1);
            json.put("sequence2", sequence2);

            logger.info(String.format("Send to consumer '%s'", json.toString()));
            String response = (String) template.convertSendAndReceive("query", json.toString());
            logger.info(String.format("Received on producer '%s'", response));

            JSONObject responseJson = new JSONObject(response);
            mav.addObject("existence", responseJson.get("existence"));
            mav.addObject("extension", responseJson.get("extension"));
            mav.addObject("result1", responseJson.get("result1"));
            mav.addObject("result2", responseJson.get("result2"));
            mav.addObject("length", responseJson.get("length"));
            mav.addObject("identities", responseJson.get("identities"));
            mav.addObject("gaps", responseJson.get("gaps"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mav;
    }
}