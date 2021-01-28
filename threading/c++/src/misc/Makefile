# 
# app/src/misc
#
BOOST_ROOT := /usr/local
BOOST_INC := ${BOOST_ROOT}/include
#LDFLAGS  := -L./boost/stage/lib -L/usr/lib -lstdc++ -lm

SERVER := app-server
CLIENT := app-client

CXX      := -g++
#CXXFLAGS := -pedantic-errors -Wall -Wextra -Werror
#CXXFLAGS := -Wall -Wc++11-extensions -Wc++11-long-long
CXXFLAGS := -g -w -Wfatal-errors
LDFLAGS  := -L/usr/lib -L/usr/local/lib -lstdc++ -std=c++11 -lm -lboost_program_options-mt -lboost_system
BUILD    := ./build
OBJ_DIR  := $(BUILD)/objects
APP_DIR  := $(BUILD)/apps
TARGET   := $(CLIENT)
INCLUDE  := -Iinclude/ -I$(BOOST_INC)/
SRC      :=                      \
   $(wildcard src/module1/*.cpp) \
   $(wildcard src/module2/*.cpp) \
   $(wildcard src/module3/*.cpp) \
   $(wildcard src/*.cpp)

OBJECTS := $(SRC:%.cpp=$(OBJ_DIR)/%.o)

all: build $(APP_DIR)/$(TARGET)

$(OBJ_DIR)/%.o: %.cpp
	@mkdir -p $(@D)
	$(CXX) $(CXXFLAGS) $(INCLUDE) -o $@ -c $<

$(APP_DIR)/$(TARGET): $(OBJECTS)
	@mkdir -p $(@D)
	$(CXX) $(CXXFLAGS) $(INCLUDE) $(LDFLAGS) -o $(APP_DIR)/$(TARGET) $(OBJECTS)

.PHONY: all build clean debug release

build:
	@mkdir -p $(APP_DIR)
	@mkdir -p $(OBJ_DIR)

debug: CXXFLAGS += -DDEBUG -g
debug: all

release: CXXFLAGS += -O2
release: all

server: $(APP_DIR)/$(SERVER)
	$(APP_DIR)/$(SERVER)

client: $(APP_DIR)/$(CLIENT)
	$(APP_DIR)/$(CLIENT)

clean:
	-@rm -rvf $(OBJ_DIR)/*
	-@rm -rvf $(APP_DIR)/*

#run: $(APP_DIR)/$(TARGET)
#	$(APP_DIR)/$(TARGET)

