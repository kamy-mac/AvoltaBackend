import { Request, Response } from 'express';
import { PublicationService } from '../services/PublicationService';
import { CreatePublicationDTO, UpdatePublicationDTO } from '../dto/PublicationDTO';

export class PublicationController {
  constructor(private publicationService: PublicationService) {}

  async getAll(req: Request, res: Response) {
    try {
      const publications = await this.publicationService.findAll();
      res.json(publications);
    } catch (error) {
      res.status(500).json({ message: 'Erreur lors de la récupération des publications' });
    }
  }

  async getById(req: Request, res: Response) {
    try {
      const publication = await this.publicationService.findById(req.params.id);
      if (!publication) {
        return res.status(404).json({ message: 'Publication non trouvée' });
      }
      res.json(publication);
    } catch (error) {
      res.status(500).json({ message: 'Erreur lors de la récupération de la publication' });
    }
  }

  async create(req: Request, res: Response) {
    try {
      const dto: CreatePublicationDTO = req.body;
      if (!req.user) {
        return res.status(400).json({ message: 'Utilisateur non authentifié' });
      }
      const publication = await this.publicationService.create(dto, req.user.id);
      res.status(201).json(publication);
    } catch (error) {
      res.status(500).json({ message: 'Erreur lors de la création de la publication' });
    }
  }

  async update(req: Request, res: Response) {
    try {
      const dto: UpdatePublicationDTO = req.body;
      const publication = await this.publicationService.update(req.params.id, dto);
      res.json(publication);
    } catch (error) {
      res.status(500).json({ message: 'Erreur lors de la mise à jour de la publication' });
    }
  }

  async delete(req: Request, res: Response) {
    try {
      await this.publicationService.delete(req.params.id);
      res.status(204).send();
    } catch (error) {
      res.status(500).json({ message: 'Erreur lors de la suppression de la publication' });
    }
  }

  async approve(req: Request, res: Response) {
    try {
      const publication = await this.publicationService.approve(req.params.id);
      res.json(publication);
    } catch (error) {
      res.status(500).json({ message: 'Erreur lors de l\'approbation de la publication' });
    }
  }

  async reject(req: Request, res: Response) {
    try {
      await this.publicationService.reject(req.params.id);
      res.status(204).send();
    } catch (error) {
      res.status(500).json({ message: 'Erreur lors du rejet de la publication' });
    }
  }
}