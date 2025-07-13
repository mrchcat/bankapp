FROM hashicorp/consul:1.21.0
RUN mkdir /tmp/bootstrap

COPY bootstrap/* /tmp/bootstrap/
RUN chmod 755 /tmp/bootstrap/*
RUN dos2unix /tmp/bootstrap/*

CMD /tmp/bootstrap/start.sh
