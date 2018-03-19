#include "mygbn.h"

void mygbn_init_sender(struct mygbn_sender* mygbn_sender, char* ip, int port, int N, int timeout){
  int fd=socket(AF_INET,SOCK_DGRAM,0);
  mygbn_sender->sd=fd;
  mygbn_sender->ip=ip;
  mygbn_sender->port=port;
  mygbn_sender->N=N;
  mygbn_sender->timeout=timeout;

}
struct MYGBN_Packet makepacks(int type,int len)
{
  struct MYGBN_Packet DataPacket;
  char gbn[3]="gbn";
  int z;
  for(z=0;z<3;z++)
  DataPacket.protocol[z]=gbn[z];
//dp=0xa0 ap=0xa1 ep=0xa2

    DataPacket.type=type;
//length+seq
    DataPacket.length=12+len;
    printf("%d\n",DataPacket.length);
    return DataPacket;
}
int mygbn_send(struct mygbn_sender* mygbn_sender, unsigned char* buf, int len){

  struct sockaddr_in destination;
  memset(&destination, 0, sizeof(struct sockaddr_in));
  destination.sin_family = AF_INET;
  inet_pton(AF_INET, mygbn_sender->ip, &(destination.sin_addr));
  destination.sin_port = htons(mygbn_sender->port);
  struct MYGBN_Packet DataPacket;
    DataPacket=makepacks(0xA0,len);


  //payload
  //if len <512 send len ,otherwise divide 512
int currentpoint=0;

  while(len>0)
  {

    if(DataPacket.length>=MAX_PAYLOAD_SIZE)
    {
      int a;
      for(a=0;a<MAX_PAYLOAD_SIZE;a++)
      {
        DataPacket.payload[currentpoint+a]=buf[currentpoint+a];

      }
      DataPacket.payload[MAX_PAYLOAD_SIZE-1]='\0';
      if(sendto(mygbn_sender->sd, &DataPacket,DataPacket.length, 0, (struct sockaddr *)&destination, sizeof(destination))<0)
      {

        return -1;
      }

    }
    else
    {
      int a;

      for(a=0;a<len;a++)
      {
        DataPacket.payload[currentpoint+a]=buf[currentpoint+a];

      }

DataPacket.payload[len]='\0';
      if(sendto(mygbn_sender->sd, &DataPacket,DataPacket.length, 0, (struct sockaddr *)&destination, sizeof(destination))<0)
      {
        return -1;
      }


        fflush(stdout);
    }
currentpoint+=MAX_PAYLOAD_SIZE;
len-=MAX_PAYLOAD_SIZE;
  }

return len;
  //
}

void mygbn_close_sender(struct mygbn_sender* mygbn_sender){
 close(mygbn_sender->sd);
}

void mygbn_init_receiver(struct mygbn_receiver* mygbn_receiver, int port){
  mygbn_receiver->sd = socket(AF_INET, SOCK_DGRAM, 0);
  struct sockaddr_in address;
  memset(&address, 0, sizeof(address));
  address.sin_family = AF_INET;
  address.sin_port = htons(port);
  address.sin_addr.s_addr = htonl(INADDR_ANY);

  // Bind the socket to the address
  bind(mygbn_receiver->sd, (struct sockaddr *)&address, sizeof(struct sockaddr));
}

int mygbn_recv(struct mygbn_receiver* mygbn_receiver, unsigned char* buf, int len){
  struct MYGBN_Packet DataPacket;

  if(recvfrom(mygbn_receiver->sd, &DataPacket,sizeof(DataPacket) , 0, NULL, NULL)<0)
  {
    return -1;
  }

  int a;
  printf("%d",DataPacket.length);
    unsigned int *x = (unsigned int*)DataPacket.payload;
  for(a=0;a<sizeof(DataPacket.payload);a++)
  {

    buf[a]=x[a];
  }

  fflush(stdout);
  return DataPacket.length-12;
}

void mygbn_close_receiver(struct mygbn_receiver* mygbn_receiver) {
close(mygbn_receiver->sd);
}
