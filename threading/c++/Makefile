#
# Multi-Threaded C++ App
#

export BOOST_ROOT := /usr/local
export BOOST_INC := ${BOOST_ROOT}/include
#LDFLAGS  := -L./boost/stage/lib -L/usr/lib -lstdc++ -lm

export PROJECT_HOME := .

export CXX := -g++
#CXXFLAGS := -pedantic-errors -Wall -Wextra -Werror
#CXXFLAGS := -Wall -Wc++11-extensions -Wc++11-long-long
export CXXFLAGS := -g -w -Wfatal-errors
export LDFLAGS := -L/usr/lib -L/usr/local/lib -lstdc++ -std=c++11 -lm -lboost_program_options-mt -lboost_system
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

clean:
	-@rm -rvf $(OBJ_DIR)/*
	-@rm -rvf $(APP_DIR)/*
