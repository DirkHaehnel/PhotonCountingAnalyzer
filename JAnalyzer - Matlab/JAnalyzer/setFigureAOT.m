function setFigureAOT(name, value)
% Set Figure with name always on top

error(javachk('awt', 'setFiguresAlwaysOnTop'));
frames = java.awt.Frame.getFrames();
for ii = 1:length(frames)
    if strcmp(char(frames(ii).getTitle()), name)
        frames(ii).setAlwaysOnTop(value)
    end;
end;
clear frames;
java.lang.System.gc(); % Promts java to delete closed figures
