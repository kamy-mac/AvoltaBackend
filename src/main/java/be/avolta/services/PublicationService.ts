import { Repository } from 'typeorm';
import { Publication } from '../models/Publication';
import { CreatePublicationDTO, UpdatePublicationDTO } from '../dto/PublicationDTO';
import { NotFoundError, UnauthorizedError } from '../utils/errors';

export class PublicationService {
  constructor(private publicationRepository: Repository<Publication>) {}

  async findAll(): Promise<Publication[]> {
    return this.publicationRepository.find({
      relations: ['author', 'comments'],
      order: { createdAt: 'DESC' }
    });
  }

  async findById(id: string): Promise<Publication | null> {
    return this.publicationRepository.findOne({
      where: { id },
      relations: ['author', 'comments']
    });
  }

  async create(dto: CreatePublicationDTO, authorId: string): Promise<Publication> {
    const publication = this.publicationRepository.create({
      ...dto,
      author: { id: authorId }
    });
    return this.publicationRepository.save(publication);
  }

  async update(id: string, dto: UpdatePublicationDTO): Promise<Publication> {
    const publication = await this.findById(id);
    if (!publication) {
      throw new NotFoundError('Publication non trouvée');
    }

    Object.assign(publication, dto);
    return this.publicationRepository.save(publication);
  }

  async delete(id: string): Promise<void> {
    const result = await this.publicationRepository.delete(id);
    if (result.affected === 0) {
      throw new NotFoundError('Publication non trouvée');
    }
  }

  async approve(id: string): Promise<Publication> {
    const publication = await this.findById(id);
    if (!publication) {
      throw new NotFoundError('Publication non trouvée');
    }

    publication.status = 'published';
    return this.publicationRepository.save(publication);
  }

  async reject(id: string): Promise<void> {
    await this.delete(id);
  }
}