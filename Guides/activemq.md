
# Download

Download ActiveMQ 5.15.8 from https://activemq.apache.org/activemq-5158-release

`tar zxvf activemq-5.15.8-bin.tar.gz`

# Setup 

Edit [activemq path]/conf/activemq.xml 

    ...
    <transportConnector name="openwire" uri="tcp://0.0.0.0:61616?
    ...

Edit the 61616 to what port you want
# Starting

```
cd activemq-5.15.8-bin.tar.gz
sudo bin/activemq console
```