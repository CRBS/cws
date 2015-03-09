#!/usr/bin/python

""" Cluster Setup
This script downloads and sets up a cluster
to run kepler workflows from crbsworkflow
service.  
"""

import sys
import urllib
import os.path
import os
import commands

def install_kepler(install_dir):

  abs_install_dir = os.path.abspath(install_dir)
  print "Installing Kepler under %s" % abs_install_dir

  if not os.path.exists(abs_install_dir):
    os.makedirs(abs_install_dir)

  kepler_tarball = _download_kepler(abs_install_dir)
  kepler_dir = _uncompress_kepler(abs_install_dir,kepler_tarball)

  home_dir = os.path.expanduser("~")

  modules_tarball = _download_kepler_modules(home_dir)

  print "Downloaded ",modules_tarball

  return kepler_dir
  # Need to grab kepler.modules.tar.gz file from this link and
  # perhaps its best to run kepler once to create the base folders then dump this in?
  # write it to $HOME/KeplerData/kepler.modules
  #wget --no-check-certificate "https://googledrive.com/host/0B_BG-weuWJa3dVBFLXZWZkVSLTg"



def _uncompress_kepler(install_dir,kepler_tarball):
  cmd = "tar --directory="+install_dir+" -zxf "+kepler_tarball

  print "Running %s" % cmd
  
  (status,out) = commands.getstatusoutput(cmd)
  suffix = kepler_tarball.find('.tar.gz')
  if suffix > 0:
    return kepler_tarball[0:suffix]
  return kepler_tarball

def _download_kepler(install_dir):
  remote_kepler_binary_url = 'https://code.kepler-project.org/code/kepler/releases/installers/2.4/kepler-2.4-linux.tar.gz'
  kepler_file = 'kepler-2.4.tar.gz'

  kepler_tarball = os.path.join(install_dir,kepler_file)
  print "Please wait downloading kepler -- ",
  # retrieve kepler tar ball
  urllib.urlretrieve(remote_kepler_binary_url,filename=kepler_tarball,reporthook=_download_progress)
  sys.stdout.write("\n")

  return kepler_tarball

def _download_kepler_modules(install_dir):
  remote_modules_binary_url = 'https://googledrive.com/host/0B_BG-weuWJa3dVBFLXZWZkVSLTg'
  modules_tarball = os.path.join(install_dir,'kepler.modules.tar.gz')

  print "Please wait downloading kepler.modules -- ",
  # retrieve kepler.modules tar ball
  urllib.urlretrieve(remote_modules_binary_url,filename=modules_tarball,reporthook=_download_progress)
  sys.stdout.write("\n")
  return modules_tarball

def _download_progress(count,block_size, total_size):
  percent = int(count*block_size*100/total_size)
  sys.stdout.write("%2d%%" % percent)
  sys.stdout.write("\b\b\b")
  sys.stdout.flush()
 

def install_panfish(installDir):
  print "Installing Panfish"

def install_crbsworkflow_client(installDir,panfishBinDir,keplerBinDir):
  print "Installing crbsworkflow service client"





def main():
  print "Cluster Setup"

  args = sys.argv[1:]

  if not args:
    print 'usage [--dir dir]'
    print '\nDownloads & Installs Kepler, Panfish, and CRBS workflow client\n'
    sys.exit(1)

  if args[0] == '--dir':
    install_dir = args[1]
    del args[0:2]
  else:
    sys.exit(2)

  keplerBin = install_kepler(install_dir)
  panfishBin = install_panfish(install_dir)
  install_crbsworkflow_client(install_dir,keplerBin,panfishBin)


if __name__ == '__main__':
  main()
