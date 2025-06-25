FROM eclipse-temurin:21-jdk
ENV DEBIAN_FRONTEND=noninteractive

#if in china, use it
#RUN echo "deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ jammy main restricted universe multiverse\n\
#deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ jammy-security main restricted universe multiverse\n\
#deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ jammy-updates main restricted universe multiverse\n\
#deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ jammy-backports main restricted universe multiverse" > /etc/apt/sources.list

RUN apt-get update # for cache
RUN apt-get install -y iputils-ping net-tools && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY emsp.jar /emsp.jar
COPY start.sh /start.sh

RUN chmod +x /start.sh

ENTRYPOINT ["/start.sh"]
