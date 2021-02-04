#
# app/src/single
#

export BOOST_ROOT := $(HOME)/dev/boost_1_75_0
export BOOST_INC := ${BOOST_ROOT}
export LDFLAGS := -L$(HOME)/dev/boost_1_75_0/stage/lib
#LDFLAGS  := -L./boost/stage/lib -L/usr/lib -lstdc++ -lm

export PROJECT_HOME := ../..
export TARGET := single_node_threaded_app

export CXX := g++
#CXXFLAGS := -pedantic-errors -Wall -Wextra -Werror
#CXXFLAGS := -Wall -Wc++11-extensions -Wc++11-long-long
export CXXFLAGS := -g -w -Wfatal-errors
export LDFLAGS := $(LDFLAGS) -L/usr/lib -L/usr/local/lib -lstdc++ -std=c++11 -lm -lboost_program_options -lboost_system -lboost_thread -lpthread
export BUILD := $(PROJECT_HOME)/build
export OBJ_DIR := $(BUILD)/objects
export APP_DIR := $(BUILD)/apps
export INCLUDE := -I$(PROJECT_HOME)/include/ -I$(BOOST_INC)/

SRC := \
   $(wildcard ./*.cpp)

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

#server: $(APP_DIR)/$(SERVER)
#	$(APP_DIR)/$(SERVER)

#client: $(APP_DIR)/$(CLIENT)
#	LD_LIBRARY_PATH=$(HOME)/dev/boost_1_75_0/stage/lib $(APP_DIR)/$(CLIENT)

clean:
	-@rm -rvf $(OBJ_DIR)/*
	-@rm -rvf $(APP_DIR)/*

#run: $(APP_DIR)/$(TARGET)
#	$(APP_DIR)/$(TARGET)

