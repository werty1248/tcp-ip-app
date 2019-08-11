#pragma once

#include <iostream>
#include <winsock2.h>
#include <wchar.h>
#include <thread>
#include <string>
#include <map>
#include <vector>
#include <TCHAR.h>
#include <locale>
#include <condition_variable>

#pragma comment(lib, "Ws2_32.lib")

using namespace std;

#define MAX_BUFFER        1024
#define SERVER_PORT        4000
#define Q_SIZE			100

string toAddress(unsigned int adr);
class Sender
{
public:
	SOCKET clientSocket[10];
	int code[10];
	string name[10];
	Sender()
	{
		for (int i = 0; i < 10; i++)
		{
			code[i] = 0;
			clientSocket[i] = NULL;
			name[i] = "";
		}
	}
	void put(int num, string message)
	{
		wchar_t unimessage[MAX_BUFFER] = { NULL };
		string MSG = name[num];
		MSG.append(" : ");
		MSG.append(message);
		int nLen = MultiByteToWideChar(CP_UTF8, 0, MSG.c_str(), MSG.length(), NULL, NULL);
		MultiByteToWideChar(CP_UTF8, 0, MSG.c_str(), MSG.length(), unimessage, nLen);
		unimessage[nLen] = NULL;

		if (send(clientSocket[num], MSG.c_str(), MSG.length(), 0) > 0)
			wprintf(L"%s (%d bytes)\n", unimessage, MSG.length());
	}
	void change(int num, SOCKET socket)
	{
		clientSocket[num] = socket;
	}
	void putCode(int num, int Code)
	{
		code[num] = Code;
		char str[20] = { NULL };
		sprintf(str, "%d", Code);
		name[num] = str;
	}
	int newClient()
	{
		for (int i = 0; i < 10; i++)
		{
			if (code[i] == 0)
			{
				return i;
			}
		}
		return -1;
	}
};
class Message
{
public:
	int client;
	string str;
	Message()
	{
		client = -1;
		str = string();
	}
	Message(int num, string message)
	{
		client = num;
		str = message;
	}
};
class Circular_Q
{
	int front, end;
	Message mes[Q_SIZE];
public:
	Circular_Q() { front = 0; end = 0; }
	void push(Message message)
	{
		mes[end] = message;
		end = (end + 1) % Q_SIZE;
	}
	boolean isEmpty()
	{
		return front == end;
	}
	Message pop()
	{
		if (front == end)
			return Message(-1, string());
		Message ret = mes[front];
		front = (front + 1) % Q_SIZE;
		return ret;
	}
};
class ClientManager
{
public:
	mutex m;
	Sender S;
	Circular_Q MSG;
	Message getMessage();
	void putMessage(Message message);
};
void run(ClientManager *CM,condition_variable *cv);
void setFlag(bool f);