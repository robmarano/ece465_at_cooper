#
# app/src/
#

export BOOST_ROOT := $(HOME)/dev/boost_1_75_0
export BOOST_INC := ${BOOST_ROOT}
export LDFLAGS := -L$(HOME)/dev/boost_1_75_0/stage/lib
#LDFLAGS  := -L./boost/stage/lib -L/usr/lib -lstdc++ -lm

export PROJECT_HOME := ..

export CXX := g++
#CXXFLAGS := -pedantic-errors -Wall -Wextra -Werror
#CXXFLAGS := -Wall -Wc++11-extensions -Wc++11-long-long
export CXXFLAGS := -g -w -Wfatal-errors
export LDFLAGS := $(LDFLAGS) -L/usr/lib -L/usr/local/lib -lstdc++ -std=c++11 -lm -lboost_program_options -lboost_system -lboost_thread -lpthread
export BUILD := $(PROJECT_HOME)/build
export OBJ_DIR := $(BUILD)/objects
export APP_DIR := $(BUILD)/apps
export INCLUDE := -I$(PROJECT_HOME)/include/ -I$(BOOST_INC)/

#SUBDIRS := single distributed misc
SUBDIRS := single
TOPTARGETS := all clean

.PHONY: $(TOPTARGETS) $(SUBDIRS)

$(TOPTARGETS): $(SUBDIRS)
	
$(SUBDIRS):
	+$(MAKE) -C $@ $(MAKECMDGOALS)

clean:
	-@rm -rvf $(OBJ_DIR)/*
	-@rm -rvf $(APP_DIR)/*
