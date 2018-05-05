@echo off
del *.svg
FOR /R %%V IN (*.dot) DO (
	dot -O -Tsvg %%V
)