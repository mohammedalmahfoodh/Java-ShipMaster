      Dim WshShell, FileSystem, RegularExpression, PFound1, Dummy, TheNVFile, TheLine, ThePattern1, ThePattern2
      Dim Flag, SysName, NBTable, ThePattern3, PFound2, ThePattern4, WrkGrp, TheText, ThePattern, Match
      Dim Matches, TheMatch, NBCommand, TheNBTFile
      Const ForReading = 1
      Set WshShell = WScript.CreateObject("WScript.Shell")
      Set FileSystem = CreateObject("Scripting.FileSystemObject")
      Set RegularExpression = New RegExp
      Dummy = WshShell.Popup ("Finding Master Browser. Please Wait...",1,"Find Master Browser Utility",64)
      WshShell.Run "Cmd.exe /c Net View > C:\Windows\Temp\NetViewList.txt", 2,True
  Set TheNVFile = FileSystem.OpenTextFile("C:\Windows\Temp\NetViewList.txt", ForReading, True)
  Do While TheNVFile.AtEndOfStream <> True
  TheLine = TheNVFile.ReadLine
  ThePattern1 = "\\"
  PFound1 = FindPattern(TheLine, ThePattern1)
  If PFound1 Then
  ThePattern2 = "\\\\\w*"
  Flag = "1"
  SysName = GetPattern(TheLine, ThePattern2, Flag )
  NBTable = GetNBTable(SysName)
  ThePattern3 = "MSBROWSE"
  PFound2 = FindPattern(NBTable, ThePattern3)
  If PFound2 Then
  ThePattern4 = "\w* \w*   <1E>"
  Flag = "2"
  WrkGrp = GetPattern(NBTable, ThePattern4, Flag)
  Exit Do
  End If
  End If
  Loop
  If PFound2 Then
  Dummy = MsgBox("The computer acting as the Master Browser" & vbCrLf &_
  " for the workgroup " & WrkGrp & vbCrLf &_
  " is " & SysName, 4160, "Find Master Browser Utility")
  Else
  Dummy = MsgBox("No Master Browser found at this time. " & vbCrLf &_
  "Chances are that an election is in progress.", 4144, "Find Master Browser Tool")
  End If
  TheNVFile.Close
  FileSystem.DeleteFile("C:\windows\Temp\NetViewList.txt")
  Wscript.Quit
  Function FindPattern(TheText, ThePattern)
  RegularExpression.Pattern = ThePattern
  If RegularExpression.Test(TheText) Then
  FindPattern = "True"
  Else
  FindPattern = "False"
  End If
  End Function
  Function GetPattern(TheText, ThePattern, Flag)
  RegularExpression.Pattern = ThePattern
  Set Matches = RegularExpression.Execute(TheText)
  For Each Match in Matches
  TheMatch = Match.Value
  If Flag = "1" Then TheMatch = Mid(TheMatch, 3)
  If Flag = "2" Then TheMatch = Left(TheMatch, 15)
  Next
  GetPattern = TheMatch
  End Function
  Function GetNBTable(SysName)
  NBCommand = "nbtstat -a " & SysName
  WshShell.Run "Cmd.exe /c " & NBCommand &" > C:\Windows\Temp\NBTList.txt", 2,True
  Set TheNBTFile = FileSystem.OpenTextFile("C:\Windows\Temp\NBTList.txt", ForReading, True)
  GetNBTable = TheNBTFile.ReadAll
  TheNBTFile.Close
  FileSystem.DeleteFile("C:\Windows\Temp\NBTList.txt")
  End Function
