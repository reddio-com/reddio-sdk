REDDIO_DEV_OPTIONS=--manifest-path=reddio/Cargo.toml
OUTPUT_DIR=./output
OUTPUT_HEADER_DIR=./output/include
OUTPUT_LIB_DIR=./output/lib

export CGO_ENABLED=1

build: build-reddio build-reddio-go

release: release-reddio build-reddio-go

clean: clean-reddio
	rm -rf output

check: check-reddio build-reddio-go

test: test-reddio test-reddio-go

fmt: fmt-reddio fmt-reddio-go

lint: lint-reddio lint-reddio-go

build-reddio: generate-header
	mkdir -p $(OUTPUT_LIB_DIR)
	cargo build $(REDDIO_DEV_OPTIONS)
	cp reddio/target/debug/libreddio.* $(OUTPUT_LIB_DIR)

release-reddio: generate-header
	mkdir -p $(OUTPUT_LIB_DIR)
	cargo build $(REDDIO_DEV_OPTIONS) --release
	cp reddio/target/release/libreddio.* $(OUTPUT_LIB_DIR)

clean-reddio:
	cargo clean $(REDDIO_DEV_OPTIONS)

check-reddio:
	cargo check $(REDDIO_DEV_OPTIONS)

test-reddio:
	cargo test $(REDDIO_DEV_OPTIONS)

fmt-reddio:
	cargo fmt $(REDDIO_DEV_OPTIONS)

lint-reddio:
	cargo clippy $(REDDIO_DEV_OPTIONS)

cbindgen:
	mkdir -p $(OUTPUT_DIR)
	cargo install cbindgen --root $(OUTPUT_DIR)
	chmod +x $(OUTPUT_DIR)/bin/cbindgen

generate-header: cbindgen
	mkdir -p $(OUTPUT_HEADER_DIR)
	$(OUTPUT_DIR)/bin/cbindgen --lang c reddio/src/lib.rs -o $(OUTPUT_HEADER_DIR)/reddio.h

build-reddio-go: build-reddio
	cd reddio-go && go build

test-reddio-go: build-reddio
	cd reddio-go && go test

fmt-reddio-go:
	cd reddio-go && go fmt

lint-reddio-go:
	cd reddio-go && go vet
