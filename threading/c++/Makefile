#
# Multi-Threaded C++ App
#

export CLIENT := single_node_threaded_app


export BOOST_ROOT := $(HOME)/dev/boost_1_75_0
export BOOST_INC := ${BOOST_ROOT}
export LDFLAGS := -L$(HOME)/dev/boost_1_75_0/stage/lib
#LDFLAGS  := -L./boost/stage/lib -L/usr/lib -lstdc++ -lm

export PROJECT_HOME := .

export CXX := g++
#CXXFLAGS := -pedantic-errors -Wall -Wextra -Werror
#CXXFLAGS := -Wall -Wc++11-extensions -Wc++11-long-long
export CXXFLAGS := -g -w -Wfatal-errors
export LDFLAGS := $(LDFLAGS) -L/usr/lib -L/usr/local/lib -lstdc++ -std=c++11 -lm -lboost_program_options -lboost_system -lboost_thread -lpthread
export BUILD := ./build
export OBJ_DIR := $(BUILD)/objects
export APP_DIR := $(BUILD)/apps
export INCLUDE := -I$(PROJECT_HOME)/include/ -I$(BOOST_INC)/

#all:
#	+$(MAKE) -C src

TOPTARGETS := all clean

#SUBDIRS := $(wildcard */.)
SUBDIRS := src

.PHONY: $(TOPTARGETS) $(SUBDIRS) clean

$(TOPTARGETS): $(SUBDIRS)
	
$(SUBDIRS):
	+$(MAKE) -C $@ $(MAKECMDGOALS)

run: $(APP_DIR)/$(CLIENT)
	LD_LIBRARY_PATH=$(HOME)/dev/boost/stage/lib $(APP_DIR)/$(CLIENT)

clean:
	-@rm -rvf $(OBJ_DIR)/*
	-@rm -rvf $(APP_DIR)/*
