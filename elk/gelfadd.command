/subsystem=logging/custom-handler=GelfLogger/:add(module=biz.paluch.logging,class=biz.paluch.logging.gelf.wildfly.WildFlyGelfLogHandler,properties={ \
       host="udp:localhost", \
       port="12201", \
       version="1.0", \
       facility="java-test", \
       extractStackTrace=true, \
       filterStackTrace=true, \
       includeLogMessageParameters=true, \
       mdcProfiling=true, \
       timestampPattern="yyyy-MM-dd HH:mm:ss,SSSS", \
       maximumMessageSize=8192, \
       additionalFields="fieldName1=fieldValue1,fieldName2=fieldValue2", \
       additionalFieldTypes="fieldName1=String,fieldName2=Double,fieldName3=Long", \
       mdcFields="mdcField1,mdcField2" \
       dynamicMdcFields="mdc.*,(mdc|MDC)fields" \
       includeFullMdc=true \
})
 
/subsystem=logging/root-logger=ROOT/:write-attribute(name=handlers,value=["FILE","CONSOLE","GelfLogger"])