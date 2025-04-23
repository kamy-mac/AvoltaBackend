export interface CreatePublicationDTO {
  title: string;
  content: string;
  imageUrl: string;
  validFrom: Date;
  validTo: Date;
  category: string;
  sendNewsletter?: boolean;
}

export interface UpdatePublicationDTO {
  title?: string;
  content?: string;
  imageUrl?: string;
  validFrom?: Date;
  validTo?: Date;
  category?: string;
}