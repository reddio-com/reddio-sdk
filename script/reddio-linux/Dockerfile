# ubuntu 18.04 use glibc 2.27
FROM ubuntu:18.04
# install curl ca-certificates then cleanup
RUN apt-get update && \
    apt-get install -y curl ca-certificates gcc && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
# setup rust
ENV CARGO_HOME=/opt/rust/cargo
ENV PATH=/opt/rust/cargo/bin:/opt/java/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
RUN curl https://sh.rustup.rs -sSf | sh -s -- -y --default-toolchain stable && \
    rm -rf $CARGO_HOME/git && \
    rm -rf $CARGO_HOME/registry
# compile artifacts
COPY . /workspace
WORKDIR /workspace
RUN cd reddio && cargo build --release --target x86_64-unknown-linux-gnu
