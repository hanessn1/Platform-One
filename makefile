# Makefile for platform-one

MODULES :=
SCRIPTS_DIR := scripts

.PHONY: all build test coverage clean docker-start docker-stop

# Default target
all: build

# Start all containers
docker-start:
	docker compose up -d

# Stop all containers
docker-stop:
	docker compose down

# Build all services
build:
	@echo "Building the project..."
	mvn clean install -DskipTests

# Run all tests
test: docker-start
	@echo "Running all tests..."
	mvn test

# Run tests for a specific module
# Usage: make test-module MODULE=booking
test-module: docker-start
ifndef MODULE
	$(error "Please specify the MODULE variable. Usage: make test-module MODULE=booking")
endif
	@echo "Running tests for module $(MODULE)..."
	mvn -pl $(MODULE) test

# Clean project
clean:
	@echo "Cleaning project..."
	mvn clean