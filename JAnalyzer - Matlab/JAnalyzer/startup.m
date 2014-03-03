%STARTUPSAV   Startup file
%   Change the name of this file to STARTUP.M. The file 
%   is executed when MATLAB starts up, if it exists 
%   anywhere on the path.  In this example, the
%   MAT-file generated during quitting using FINISHSAV
%   is loaded into MATLAB during startup.

%   Copyright 1984-2002 The MathWorks, Inc. 
%   $Revision: 1.5 $  $Date: 2002/06/07 21:45:07 $

cd X:\MTusers\Christoph

set(0,'defaultFigurePosition',[100 100 660 520].*[0.2 0.5 1.5 1.2])
set(0,'defaultAxesFontName', 'Times', 'defaultAxesFontSize', 16, 'defaultLineLineWidth', 2, ...
    'defaultTextFontName', 'Times', 'defaultTextFontSize', 16);
set(0,'defaultaxeslinewidth',1)
set(0,'defaultAxesColorOrder', [1 0 0; 0 0 1; 0 0.5 0; 0.75 0.75 0; 0 0.75 0.75; 0.75 0 0.75; 0.25 0.25 0.25])
set(0,'defaultAxesLineWidth', 1.5)
set(0,'defaultLineMarkerSize',3)
